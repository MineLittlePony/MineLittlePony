package com.voxelmodpack.hdskins.skins;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.http.HttpHeaders;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class ValhallaSkinServer implements SkinServer {

    private final String baseURL;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    private String accessToken;

    private ValhallaSkinServer(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {

        try (CloseableHttpClient client = HttpClients.createSystem();
                CloseableHttpResponse response = client.execute(new HttpGet(getTexturesURI(profile)))) {

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                return Optional.of(readJson(response.getEntity().getContent(), MinecraftTexturesPayload.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable URI image,
            MinecraftProfileTexture.Type type, Map<String, String> metadata) {
        return CallableFutures.asyncFailableFuture(() -> {
            try (CloseableHttpClient client = HttpClients.createSystem()) {
                authorize(client, session);

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
        }, HDSkinManager.skinUploadExecutor);
    }

    private SkinUploadResponse resetSkin(CloseableHttpClient client, GameProfile profile, MinecraftProfileTexture.Type type) throws IOException {
        return upload(client, RequestBuilder.delete()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .build());
    }

    private SkinUploadResponse uploadFile(CloseableHttpClient client, File file, GameProfile profile, MinecraftProfileTexture.Type type,
            Map<String, String> metadata) throws IOException {
        MultipartEntityBuilder b = MultipartEntityBuilder.create();
        b.addBinaryBody("file", file, ContentType.create("image/png"), file.getName());
        metadata.forEach(b::addTextBody);

        return upload(client, RequestBuilder.put()
                .setUri(buildUserTextureUri(profile, type))
                .addHeader(HttpHeaders.AUTHORIZATION, this.accessToken)
                .setEntity(b.build())
                .build());
    }

    private SkinUploadResponse uploadUrl(CloseableHttpClient client, URI uri, GameProfile profile, MinecraftProfileTexture.Type type,
            Map<String, String> metadata) throws IOException {

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
            int code = response.getStatusLine().getStatusCode();
            JsonObject json = readJson(response.getEntity().getContent(), JsonObject.class);

            return new SkinUploadResponse(code == HttpStatus.SC_OK, json.get("message").getAsString());
        }
    }


    private void authorize(CloseableHttpClient client, Session session) throws IOException, AuthenticationException {
        if (accessToken != null) {
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

    private <T> T readJson(InputStream in, Class<T> cl) throws IOException {
        try (Reader r = new InputStreamReader(in)) {
            return gson.fromJson(r, cl);
        }
    }

    private AuthHandshake authHandshake(CloseableHttpClient client, String name) throws IOException {
        try (CloseableHttpResponse resp = client.execute(RequestBuilder.post()
                .setUri(getHandshakeURI())
                .addParameter("name", name)
                .build())) {
            return readJson(resp.getEntity().getContent(), AuthHandshake.class);
        }
    }

    private AuthResponse authResponse(CloseableHttpClient client, String name, long verifyToken) throws IOException {
        try (CloseableHttpResponse resp = client.execute(RequestBuilder.post()
                .setUri(getResponseURI())
                .addParameter("name", name)
                .addParameter("verifyToken", String.valueOf(verifyToken))
                .build())) {
            return readJson(resp.getEntity().getContent(), AuthResponse.class);
        }
    }

    private URI buildUserTextureUri(GameProfile profile, MinecraftProfileTexture.Type textureType) {
        String user = UUIDTypeAdapter.fromUUID(profile.getId());
        String skinType = textureType.name().toLowerCase(Locale.US);
        return URI.create(String.format("%s/user/%s/%s", this.baseURL, user, skinType));
    }

    private URI getTexturesURI(GameProfile profile) {
        Preconditions.checkNotNull(profile.getId(), "profile id required for skins");
        return URI.create(String.format("%s/user/%s", this.baseURL, UUIDTypeAdapter.fromUUID(profile.getId())));
    }

    private URI getHandshakeURI() {
        return URI.create(String.format("%s/auth/handshake", this.baseURL));
    }

    private URI getResponseURI() {
        return URI.create(String.format("%s/auth/response", this.baseURL));
    }

    static ValhallaSkinServer from(String server) {
        Matcher matcher = Pattern.compile("^valhalla:(.*)$").matcher(server);
        if (matcher.find()) {
            return new ValhallaSkinServer(matcher.group(1));
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("WeakerAccess")
    static class AuthHandshake {

        boolean offline;
        String serverId;
        long verifyToken;
    }

    @SuppressWarnings("WeakerAccess")
    static class AuthResponse {

        String accessToken;
        UUID userId;
    }
}
