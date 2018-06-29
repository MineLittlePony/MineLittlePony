package com.voxelmodpack.hdskins.skins;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class LegacySkinServer implements SkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    private static final Logger logger = LogManager.getLogger();

    private final String address;
    private final String gateway;

    public LegacySkinServer(String address) {
        this(address, null);
    }

    public LegacySkinServer(String address, @Nullable String gateway) {
        this.address = address;
        this.gateway = gateway;
    }

    @Override
    public Optional<MinecraftProfileTexture> getPreviewTexture(MinecraftProfileTexture.Type type, GameProfile profile) {
        if (Strings.isNullOrEmpty(this.gateway))
            return Optional.empty();
        return Optional.of(new MinecraftProfileTexture(getPath(this.gateway, type, profile), null));
    }

    @Override
    public Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        ImmutableMap.Builder<MinecraftProfileTexture.Type, MinecraftProfileTexture> builder = ImmutableMap.builder();
        for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {

            String url = getPath(this.address, type, profile);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                if (urlConnection.getResponseCode() / 100 != 2) {
                    throw new IOException("Bad response code: " + urlConnection.getResponseCode());
                }
                builder.put(type, new MinecraftProfileTexture(url, null));
                logger.debug("Found skin for {} at {}", profile.getName(), url);
            } catch (IOException e) {
                logger.trace("Couldn't find texture for {} at {}. Does it exist?", profile.getName(), url, e);
            }
        }

        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = builder.build();
        if (map.isEmpty()) {
            logger.debug("No textures found for {} at {}", profile, this.address);
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
    public ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type, boolean thinSkinType) {

        if (Strings.isNullOrEmpty(this.gateway))
            return Futures.immediateFailedFuture(new NullPointerException("gateway url is blank"));

        return HDSkinManager.skinUploadExecutor.submit(() -> {
            verifyServerConnection(session, SERVER_ID);

            Map<String, ?> data = image == null ? getClearData(session, type) : getUploadData(session, type, (thinSkinType ? "thin" : "default"), image);
            ThreadMultipartPostUpload upload = new ThreadMultipartPostUpload(this.gateway, data);
            String response = upload.uploadMultipart();
            return new SkinUploadResponse(response.equalsIgnoreCase("OK"), response);
        });
    }

    private static Map<String, ?> getData(Session session, MinecraftProfileTexture.Type type, String model, String param, Object val) {
        return ImmutableMap.of(
                "user", session.getUsername(),
                "uuid", session.getPlayerID(),
                "type", type.toString().toLowerCase(Locale.US),
                "model", model,
                param, val);
    }

    private static Map<String, ?> getClearData(Session session, MinecraftProfileTexture.Type type) {
        return getData(session, type, "default", "clear", "1");
    }

    private static Map<String, ?> getUploadData(Session session, MinecraftProfileTexture.Type type, String model, Path skinFile) {
        return getData(session, type, model, type.toString().toLowerCase(Locale.US), skinFile);
    }

    private static String getPath(String address, MinecraftProfileTexture.Type type, GameProfile profile) {
        String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
        String path = type.toString().toLowerCase() + "s";
        return String.format("%s/%s/%s.png", address, path, uuid);
    }

    private static void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }

    /**
     * Should be in the format {@code legacy:http://address;http://gateway}. Gateway is optional.
     */
    static LegacySkinServer from(String parsed) {
        Matcher matcher = Pattern.compile("^legacy:(.+?)(?:;(.*))?$").matcher(parsed);
        if (matcher.find()) {
            String addr = matcher.group(1);
            String gate = matcher.group(2);
            return new LegacySkinServer(addr, gate);
        }
        throw new IllegalArgumentException("server format string was not correct");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("gateway", gateway)
                .toString();
    }
}
