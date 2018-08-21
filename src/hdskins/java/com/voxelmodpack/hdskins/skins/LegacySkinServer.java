package com.voxelmodpack.hdskins.skins;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

@ServerType("legacy")
public class LegacySkinServer implements SkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    private static final Logger logger = LogManager.getLogger();

    @Expose
    private final String address;

    @Expose
    private final String gateway;

    public LegacySkinServer(String address, @Nullable String gateway) {
        this.address = address;
        this.gateway = gateway;
    }

    @Override
    public CompletableFuture<MinecraftTexturesPayload> getPreviewTextures(GameProfile profile) {
        try {
            SkinServer.verifyServerConnection(Minecraft.getMinecraft().getSession(), SERVER_ID);
        } catch (Exception e) {
            return CallableFutures.failedFuture(e);
        }

        if (Strings.isNullOrEmpty(gateway)) {
            return CallableFutures.failedFuture(gatewayUnsupported());
        }
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new EnumMap<>(MinecraftProfileTexture.Type.class);
        for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {
            map.put(type, new MinecraftProfileTexture(getPath(gateway, type, profile), null));
        }
        return CompletableFuture.completedFuture(TexturesPayloadBuilder.createTexturesPayload(profile, map));
    }

    @Override
    public MinecraftTexturesPayload loadProfileData(GameProfile profile) throws IOException {
        ImmutableMap.Builder<MinecraftProfileTexture.Type, MinecraftProfileTexture> builder = ImmutableMap.builder();
        for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {

            String url = getPath(address, type, profile);
            try {
                builder.put(type, loadProfileTexture(profile, url));
            } catch (IOException e) {
                logger.trace("Couldn't find texture for {} at {}. Does it exist?", profile.getName(), url, e);
            }
        }

        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = builder.build();
        if (map.isEmpty()) {
            throw new IOException(String.format("No textures found for %s at %s", profile, this.address));
        }
        return TexturesPayloadBuilder.createTexturesPayload(profile, map);
    }

    private MinecraftProfileTexture loadProfileTexture(GameProfile profile, String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        if (urlConnection.getResponseCode() / 100 != 2) {
            throw new IOException("Bad response code: " + urlConnection.getResponseCode() + ". URL: " + url);
        }
        logger.debug("Found skin for {} at {}", profile.getName(), url);
        return new MinecraftProfileTexture(url, null);
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, SkinUpload upload) {
        if (Strings.isNullOrEmpty(gateway)) {
            return CallableFutures.failedFuture(gatewayUnsupported());
        }

        return CallableFutures.asyncFailableFuture(() -> {
            SkinServer.verifyServerConnection(session, SERVER_ID);

            NetClient client = new NetClient("POST", gateway);

            client.putFormData(createHeaders(session, upload), "image/png");

            if (upload.getImage() != null) {
                client.putFile(upload.getType().toString().toLowerCase(Locale.US), "image/png", upload.getImage());
            }

            String response = client.send().text();

            if (response.startsWith("ERROR: ")) {
                response = response.substring(7);
            }
            if (!response.equalsIgnoreCase("OK") && !response.endsWith("OK")) {
                throw new IOException(response);
            }
            return new SkinUploadResponse(response);

        }, HDSkinManager.skinUploadExecutor);
    }

    private UnsupportedOperationException gatewayUnsupported() {
        return new UnsupportedOperationException("Server does not have a gateway.");
    }

    protected Map<String, ?> createHeaders(Session session, SkinUpload upload) {
        Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("user", session.getUsername())
                .put("uuid", UUIDTypeAdapter.fromUUID(session.getProfile().getId()))
                .put("type", upload.getType().toString().toLowerCase(Locale.US))
                .put("model", upload.getMetadata().getOrDefault("model", "default"));

        if (upload.getImage() == null) {
            builder.put("clear", "1");
        }

        return builder.build();
    }

    private static String getPath(String address, MinecraftProfileTexture.Type type, GameProfile profile) {
        String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
        String path = type.toString().toLowerCase() + "s";
        return String.format("%s/%s/%s.png", address, path, uuid);
    }

    @Override
    public boolean verifyGateway() {
        return !Strings.isNullOrEmpty(gateway);
    }

    @Override
    public String toString() {
        return new IndentedToStringStyle.Builder(this)
                .append("address", address)
                .append("gateway", gateway)
                .build();
    }
}
