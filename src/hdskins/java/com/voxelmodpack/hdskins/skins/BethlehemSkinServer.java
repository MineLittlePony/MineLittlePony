package com.voxelmodpack.hdskins.skins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.upload.ThreadMultipartPostUpload;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class BethlehemSkinServer implements SkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    private static final Logger logger = LogManager.getLogger();

    private final String gateway;

    private BethlehemSkinServer(String address) {
        gateway = address;
    }

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    public MinecraftTexturesPayload getProfileData(GameProfile profile) {

        String url = getPath(profile);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection(Minecraft.getMinecraft().getProxy());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.connect();

            if (urlConnection.getResponseCode() / 100 != 2) {
                throw new IOException("Bad response code: " + urlConnection.getResponseCode());
            }



            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder builder = new StringBuilder();

            int ch;
            while ((ch = reader.read()) != -1) {
                builder.append((char)ch);
            }

            String json = builder.toString();

            JsonObject s = gson.fromJson(json, JsonObject.class);

            if (s.has("success") && s.get("success").getAsBoolean()) {
                s = s.get("data").getAsJsonObject();

                return gson.fromJson(s, MinecraftTexturesPayload.class);
            }
        } catch (IOException e) {
            logger.trace("Couldn't reach skin server for {} at {}", profile.getName(), url, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            IOUtils.closeQuietly(reader);
        }

        return null;
    }

    public MinecraftProfileTexture getPreview(Type type, GameProfile profile) {
        MinecraftTexturesPayload payload = getProfileData(profile);

        if (payload != null && payload.getTextures().containsKey(type)) {
            return payload.getTextures().get(type);
        }

        return null;
    }

    @Override
    public final Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        return Optional.ofNullable(getProfileData(profile));
    }

    @Override
    public final Optional<MinecraftProfileTexture> getPreviewTexture(Type type, GameProfile profile) {
        return Optional.ofNullable(getPreview(type, profile));
    }

    @Override
    public ListenableFuture<SkinUploadResponse> uploadSkin(Session session, @Nullable Path image, MinecraftProfileTexture.Type type, boolean thinSkinType) {

        if (Strings.isNullOrEmpty(gateway))
            return Futures.immediateFailedFuture(new NullPointerException("gateway url is blank"));

        return HDSkinManager.skinUploadExecutor.submit(() -> {
            verifyServerConnection(session, SERVER_ID);

            Map<String, ?> data = image == null ? getClearData(session, type) : getUploadData(session, type, (thinSkinType ? "slim" : "default"), image);

            ThreadMultipartPostUpload upload = new ThreadMultipartPostUpload(gateway, data);

            String response = upload.uploadMultipart();

            return new SkinUploadResponse(response.equalsIgnoreCase("OK"), response);
        });
    }

    protected static ImmutableMap.Builder<String, Object> getData(Session session, MinecraftProfileTexture.Type type) {
        return ImmutableMap.<String, Object>builder()
                    .put("accessToken", session.getToken())
                    .put("user", session.getUsername())
                    .put("uuid", UUIDTypeAdapter.fromUUID(session.getProfile().getId()))
                    .put("type", type.toString().toLowerCase(Locale.US));
    }

    protected static Map<String, ?> getClearData(Session session, MinecraftProfileTexture.Type type) {
        return getData(session, type)
                .put("clear", "1")
                .build();
    }

    protected static Map<String, ?> getUploadData(Session session, MinecraftProfileTexture.Type type, String model, Path skinFile) {
        return getData(session, type)
                .put("model", model)
                .put(type.toString().toLowerCase(Locale.US), skinFile)
                .build();
    }

    private String getPath(GameProfile profile) {

        String uuid = UUIDTypeAdapter.fromUUID(profile.getId());

        return String.format("%s/profile/%s", gateway, uuid);
    }

    protected static void verifyServerConnection(Session session, String serverId) throws AuthenticationException {
        MinecraftSessionService service = Minecraft.getMinecraft().getSessionService();
        service.joinServer(session.getProfile(), session.getToken(), serverId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("gateway", gateway)
                .toString();
    }

    public static BethlehemSkinServer from(String parsed) {
        Matcher matcher = Pattern.compile("^bethlehem:(.+?)$").matcher(parsed);
        if (matcher.find()) {
            String addr = matcher.group(1);

            return new BethlehemSkinServer(addr);
        }
        throw new IllegalArgumentException("server format string was not correct");
    }
}
