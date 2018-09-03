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
import com.voxelmodpack.hdskins.util.TexturesPayloadBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpHead;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Date;
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
        return CallableFutures.asyncFailableFuture(() -> {
            SkinServer.verifyServerConnection(Minecraft.getMinecraft().getSession(), SERVER_ID);

            if (Strings.isNullOrEmpty(gateway)) {
                throw gatewayUnsupported();
            }

            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new EnumMap<>(MinecraftProfileTexture.Type.class);
            for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {
                map.put(type, new MinecraftProfileTexture(getPath(gateway, type, profile), null));
            }

            return TexturesPayloadBuilder.createTexturesPayload(profile, map);
        }, HDSkinManager.skinDownloadExecutor);
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
        try (MoreHttpResponses resp = MoreHttpResponses.execute(HDSkinManager.httpClient, new HttpHead(url))) {
            if (!resp.ok()) {
                throw new IOException("Bad response code: " + resp.getResponseCode() + ". URL: " + url);
            }
            logger.debug("Found skin for {} at {}", profile.getName(), url);

            Header eTagHeader = resp.getResponse().getFirstHeader(HttpHeaders.ETAG);
            final String eTag = eTagHeader == null ? "" : StringUtils.strip(eTagHeader.getValue(), "\"");

            // Add the ETag onto the end of the texture hash. Should properly cache the textures.
            return new MinecraftProfileTexture(url, null) {

                @Override
                public String getHash() {
                    return super.getHash() + eTag;
                }
            };
        }
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

    private Map<String, ?> createHeaders(Session session, SkinUpload upload) {
        Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("user", session.getUsername())
                .put("uuid", UUIDTypeAdapter.fromUUID(session.getProfile().getId()))
                .put("type", upload.getType().toString().toLowerCase(Locale.US));

        if (upload.getImage() == null) {
            builder.put("clear", "1");
        }

        return builder.build();
    }

    private static String getPath(String address, MinecraftProfileTexture.Type type, GameProfile profile) {
        String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
        String path = type.toString().toLowerCase() + "s";
        return String.format("%s/%s/%s.png?%s", address, path, uuid, Long.toString(new Date().getTime() / 1000));
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
