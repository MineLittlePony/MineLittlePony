package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.upload.ThreadMultipartPostUpload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public class LegacySkinServer extends AbstractSkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    private static final Logger logger = LogManager.getLogger();

    private final String address;
    private final String gateway;

    public LegacySkinServer(String address, String gateway) {
        this.address = address;
        this.gateway = gateway;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getGateway() {
        return gateway;
    }

    @Override
    public MinecraftProfileTexture getPreviewTexture(MinecraftProfileTexture.Type type, GameProfile profile) {
        return new MinecraftProfileTexture(getPath(getGateway(), type, profile), null);
    }

    @Override
    public Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        ImmutableMap.Builder<MinecraftProfileTexture.Type, MinecraftProfileTexture> builder = ImmutableMap.builder();
        for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {

            String url = getPath(getAddress(), type, profile);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                if (urlConnection.getResponseCode() / 100 != 2) {
                    throw new IOException("Bad response code: " + urlConnection.getResponseCode());
                }
                builder.put(type, new MinecraftProfileTexture(url, null));
                logger.info("Found skin for {} at {}", profile.getName(), url);
            } catch (IOException e) {
                logger.debug("Couldn't find texture at {}. Does it exist?", url, e);
            }

        }

        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = builder.build();
        if (map.isEmpty()) {
            logger.debug("No textures found for {} at {}", profile, this.getAddress());
            return Optional.empty();
        }

        return Optional.of(new TexturesPayloadBuilder()
                .profileId(profile.getId())
                .profileName(profile.getName())
                .timestamp(System.currentTimeMillis())
                .isPublic(true)
                .textures(map)
                .build());
    }

    @Override
    public ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type) {

        return skinUploadExecutor.submit(() -> {
            verifyServerConnection(session, SERVER_ID);

            Map<String, ?> data = image == null ? getClearData(session, type) : getUploadData(session, type, image);
            ThreadMultipartPostUpload upload = new ThreadMultipartPostUpload(getGateway(), data);
            String response = upload.uploadMultipart();
            return new SkinUploadResponse(response.equalsIgnoreCase("OK"), response);
        });
    }

    private Map<String, ?> getData(Session session, MinecraftProfileTexture.Type type, String param, Object val) {
        return ImmutableMap.of(
                "user", session.getUsername(),
                "uuid", session.getPlayerID(),
                "type", type.toString().toLowerCase(Locale.US),
                param, val);
    }

    private Map<String, ?> getClearData(Session session, MinecraftProfileTexture.Type type) {
        return getData(session, type, "clear", "1");
    }

    private Map<String, ?> getUploadData(Session session, MinecraftProfileTexture.Type type, Path skinFile) {
        return getData(session, type, type.toString().toLowerCase(Locale.US), skinFile);
    }

    private String getPath(String address, MinecraftProfileTexture.Type type, GameProfile profile) {
        String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
        String path = type.toString().toLowerCase() + "s";
        return String.format("%s/%s/%s.png", address, path, uuid);
    }

    private void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }

}
