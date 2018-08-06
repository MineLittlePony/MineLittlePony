package com.voxelmodpack.hdskins.skins;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@ServerType("valhalla")
public class ValhallaSkinServer extends AbstractSkinServer {

    private transient String accessToken;

    public ValhallaSkinServer(String address) {
        super(address);
    }


    @Override
    public MinecraftTexturesPayload getProfileData(GameProfile profile) throws IOException {

        try (MoreHttpResponses response = MoreHttpResponses.execute(NetClient.nativeClient(), new HttpGet(getTexturesURI(profile)))) {

            if (response.ok()) {
                return readJson(response, MinecraftTexturesPayload.class);
            }
            throw new IOException("Server sent non-ok response code: " + response.getResponseCode());
        }
    }

    @Override
    protected SkinUploadResponse doUpload(Session session, SkinUpload skin) throws AuthenticationException, IOException {
        URI image = skin.getImage();
        Map<String, String> metadata = skin.getMetadata();
        MinecraftProfileTexture.Type type = skin.getType();

        authorize(session);

        try {
            return upload(session, image, type, metadata);
        } catch (IOException e) {
            if (e.getMessage().equals("Authorization failed")) {
                accessToken = null;
                authorize(session);
                return upload(session, image, type, metadata);
            }
            throw e;
        }
    }

    private SkinUploadResponse upload(Session session, @Nullable URI image,
            MinecraftProfileTexture.Type type, Map<String, String> metadata)
            throws IOException {
        GameProfile profile = session.getProfile();

        if (image == null) {
            return resetSkin(profile, type);
        }
        switch (image.getScheme()) {
            case "file":
                return uploadFile(new File(image), profile, type, metadata);
            case "http":
            case "https":
                return uploadUrl(image, profile, type, metadata);
            default:
                throw new IOException("Unsupported URI scheme: " + image.getScheme());
        }
    }

    private SkinUploadResponse resetSkin(GameProfile profile, MinecraftProfileTexture.Type type) throws IOException {
        return upload(RequestBuilder.delete()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .build());
    }

    private SkinUploadResponse uploadFile(File file, GameProfile profile, MinecraftProfileTexture.Type type, Map<String, String> metadata) throws IOException {
        MultipartEntityBuilder b = MultipartEntityBuilder.create();
        b.addBinaryBody("file", file, ContentType.create("image/png"), file.getName());
        metadata.forEach(b::addTextBody);

        return upload(RequestBuilder.put()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .setEntity(b.build())
                .build());
    }

    private SkinUploadResponse uploadUrl(URI uri, GameProfile profile, MinecraftProfileTexture.Type type, Map<String, String> metadata) throws IOException {

        return upload(RequestBuilder.post()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .addParameter("file", uri.toString())
                .addParameters(metadata.entrySet().stream()
                        .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                        .toArray(NameValuePair[]::new))
                .build());
    }

    private SkinUploadResponse upload(HttpUriRequest request) throws IOException {
        try (MoreHttpResponses response = MoreHttpResponses.execute(NetClient.nativeClient(), request)) {
            return readJson(response, SkinUploadResponse.class);
        }
    }

    private void authorize(Session session) throws IOException, AuthenticationException {
        if (this.accessToken != null) {
            return;
        }

        GameProfile profile = session.getProfile();

        AuthHandshake handshake = authHandshake(profile.getName());

        if (handshake.offline) {
            return;
        }

        // join the session server
        Minecraft.getMinecraft().getSessionService().joinServer(profile, session.getToken(), handshake.serverId);

        AuthResponse response = authResponse(profile.getName(), handshake.verifyToken);
        if (!response.userId.equals(profile.getId())) {
            throw new IOException("UUID mismatch!"); // probably won't ever throw
        }

        accessToken = response.accessToken;
    }

    private <T> T readJson(MoreHttpResponses resp, Class<T> cl) throws IOException {
        String type = resp.getResponse().getEntity().getContentType().getValue();
        if (!"application/json".equals(type)) {
            throw new IOException("Server returned a non-json response!");
        }

        if (resp.ok()) {
            return resp.json(cl);
        }
        throw new IOException(resp.json(JsonObject.class).get("message").getAsString());

    }

    private AuthHandshake authHandshake(String name) throws IOException {
        try (MoreHttpResponses resp = MoreHttpResponses.execute(NetClient.nativeClient(), RequestBuilder.post()
                .setUri(getHandshakeURI())
                .addParameter("name", name)
                .build())) {
            return readJson(resp, AuthHandshake.class);
        }
    }

    private AuthResponse authResponse(String name, long verifyToken) throws IOException {
        try (MoreHttpResponses resp = MoreHttpResponses.execute(NetClient.nativeClient(), RequestBuilder.post()
                .setUri(getResponseURI())
                .addParameter("name", name)
                .addParameter("verifyToken", String.valueOf(verifyToken))
                .build())) {
            return readJson(resp, AuthResponse.class);
        }
    }

    private URI buildUserTextureUri(GameProfile profile, MinecraftProfileTexture.Type textureType) {
        String user = UUIDTypeAdapter.fromUUID(profile.getId());
        String skinType = textureType.name().toLowerCase(Locale.US);
        return URI.create(String.format("%s/user/%s/%s", address, user, skinType));
    }

    private URI getTexturesURI(GameProfile profile) {
        Preconditions.checkNotNull(profile.getId(), "profile id required for skins");
        return URI.create(String.format("%s/user/%s", address, UUIDTypeAdapter.fromUUID(profile.getId())));
    }

    private URI getHandshakeURI() {
        return URI.create(String.format("%s/auth/handshake", address));
    }

    private URI getResponseURI() {
        return URI.create(String.format("%s/auth/response", address));
    }

    @SuppressWarnings("WeakerAccess")
    private static class AuthHandshake {

        private boolean offline;
        private String serverId;
        private long verifyToken;
    }

    @SuppressWarnings("WeakerAccess")
    private static class AuthResponse {

        private String accessToken;
        private UUID userId;
    }
}
