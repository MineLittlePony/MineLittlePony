package com.voxelmodpack.hdskins.skins;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

@ServerType("valhalla")
public class ValhallaSkinServer implements SkinServer {

    @Expose
    private final String address;

    private transient String accessToken;

    public ValhallaSkinServer(String address) {
        this.address = address;
    }

    @Override
    public Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {

        try (CloseableHttpClient client = HttpClients.createSystem();
                CloseableHttpResponse response = client.execute(new HttpGet(getTexturesURI(profile)))) {

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                return Optional.of(readJson(response, MinecraftTexturesPayload.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable URI image, MinecraftProfileTexture.Type type, Map<String, String> metadata) {
        return CallableFutures.asyncFailableFuture(() -> {
            try (CloseableHttpClient client = HttpClients.createSystem()) {
                authorize(client, session);

                try {
                    return upload(client, session, image, type, metadata);
                } catch (IOException e) {
                    if (e.getMessage().equals("Authorization failed")) {
                        accessToken = null;
                        authorize(client, session);
                        return upload(client, session, image, type, metadata);
                    }
                    throw e;
                }
            }
        }, HDSkinManager.skinUploadExecutor);
    }

    private SkinUploadResponse upload(CloseableHttpClient client, Session session, @Nullable URI image,
            MinecraftProfileTexture.Type type, Map<String, String> metadata)
            throws IOException {
        GameProfile profile = session.getProfile();

        if (image == null) {
            return resetSkin(client, profile, type);
        }
        switch (image.getScheme()) {
            case "file":
                return uploadFile(client, new File(image), profile, type, metadata);
            case "http":
            case "https":
                return uploadUrl(client, image, profile, type, metadata);
            default:
                throw new IOException("Unsupported URI scheme: " + image.getScheme());
        }

    }

    private SkinUploadResponse resetSkin(CloseableHttpClient client, GameProfile profile, MinecraftProfileTexture.Type type) throws IOException {
        return upload(client, RequestBuilder.delete()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .build());
    }

    private SkinUploadResponse uploadFile(CloseableHttpClient client, File file, GameProfile profile, MinecraftProfileTexture.Type type, Map<String, String> metadata) throws IOException {
        MultipartEntityBuilder b = MultipartEntityBuilder.create();
        b.addBinaryBody("file", file, ContentType.create("image/png"), file.getName());
        metadata.forEach(b::addTextBody);

        return upload(client, RequestBuilder.put()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .setEntity(b.build())
                .build());
    }

    private SkinUploadResponse uploadUrl(CloseableHttpClient client, URI uri, GameProfile profile, MinecraftProfileTexture.Type type, Map<String, String> metadata) throws IOException {

        return upload(client, RequestBuilder.post()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .addParameter("file", uri.toString())
                .addParameters(metadata.entrySet().stream()
                        .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                        .toArray(NameValuePair[]::new))
                .build());
    }

    private SkinUploadResponse upload(CloseableHttpClient client, HttpUriRequest request) throws IOException {
        try (CloseableHttpResponse response = client.execute(request)) {
            return readJson(response, SkinUploadResponse.class);
        }
    }


    private void authorize(CloseableHttpClient client, Session session) throws IOException, AuthenticationException {
        if (this.accessToken != null) {
            return;
        }
        GameProfile profile = session.getProfile();
        String token = session.getToken();
        AuthHandshake handshake = authHandshake(client, profile.getName());

        if (handshake.offline) {
            return;
        }

        // join the session server
        Minecraft.getMinecraft().getSessionService().joinServer(profile, token, handshake.serverId);

        AuthResponse response = authResponse(client, profile.getName(), handshake.verifyToken);
        if (!response.userId.equals(profile.getId())) {
            throw new IOException("UUID mismatch!"); // probably won't ever throw
        }
        this.accessToken = response.accessToken;
    }

    private <T> T readJson(HttpResponse resp, Class<T> cl) throws IOException {
        String type = resp.getEntity().getContentType().getValue();
        if (!"application/json".equals(type)) {
            try {
                throw new IOException("Server returned a non-json response!");
            } finally {
                EntityUtils.consumeQuietly(resp.getEntity());
            }
        }
        try (Reader r = new InputStreamReader(resp.getEntity().getContent())) {
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // TODO specific error handling
                throw new IOException(gson.fromJson(r, JsonObject.class).get("message").getAsString());
            }
            return gson.fromJson(r, cl);
        }
    }

    private AuthHandshake authHandshake(CloseableHttpClient client, String name) throws IOException {
        try (CloseableHttpResponse resp = client.execute(RequestBuilder.post()
                .setUri(getHandshakeURI())
                .addParameter("name", name)
                .build())) {
            return readJson(resp, AuthHandshake.class);
        }
    }

    private AuthResponse authResponse(CloseableHttpClient client, String name, long verifyToken) throws IOException {
        try (CloseableHttpResponse resp = client.execute(RequestBuilder.post()
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
        return URI.create(String.format("%s/user/%s/%s", this.address, user, skinType));
    }

    private URI getTexturesURI(GameProfile profile) {
        Preconditions.checkNotNull(profile.getId(), "profile id required for skins");
        return URI.create(String.format("%s/user/%s", this.address, UUIDTypeAdapter.fromUUID(profile.getId())));
    }

    private URI getHandshakeURI() {
        return URI.create(String.format("%s/auth/handshake", this.address));
    }

    private URI getResponseURI() {
        return URI.create(String.format("%s/auth/response", this.address));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, IndentedToStringStyle.INSTANCE)
                .append("address", this.address)
                .toString();
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
