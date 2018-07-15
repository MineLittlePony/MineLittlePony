package com.voxelmodpack.hdskins.server;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.util.NetClient;

import net.minecraft.util.Session;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

@ServerType("legacy")
public class LegacySkinServer extends AbstractSkinServer {

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
    public Map<Type, MinecraftProfileTexture> getPreviewTextures(GameProfile profile) {
        if (Strings.isNullOrEmpty(gateway)) {
            return Collections.emptyMap();
        }

        Map<Type, MinecraftProfileTexture> map = new EnumMap<>(Type.class);

        for (Type type : Type.values()) {
            map.put(type, new MinecraftProfileTexture(getPath(gateway, type, profile), null));
        }

        return map;
    }

    @SuppressWarnings("deprecation")
    @Override
    public MinecraftTexturesPayload getProfileData(GameProfile profile) {
        ImmutableMap.Builder<Type, MinecraftProfileTexture> builder = ImmutableMap.builder();

        for (Type type : Type.values()) {
            String url = getPath(address, type, profile);

            try (NetClient client = new NetClient("GET", url)) {
                if (!client.send()) {
                    throw new IOException("Bad response code: " + client.getResponseCode());
                }

                builder.put(type, new MinecraftProfileTexture(url, null));
                logger.debug("Found skin for {} at {}", profile.getName(), url);
            } catch (IOException e) {
                logger.trace("Couldn't find texture for {} at {}. Does it exist?", profile.getName(), url, e);
            }
        }

        Map<Type, MinecraftProfileTexture> map = builder.build();

        if (map.isEmpty()) {
            logger.debug("No textures found for {} at {}", profile, address);
            return null;
        }

        return TexturesPayloadBuilder.createTexuresPayload(profile, map);
    }

    @Override
    public SkinUploadResponse doUpload(Session session, URI image, Type type, Map<String, String> metadata) throws AuthenticationException, IOException {
        SkinServer.verifyServerConnection(session, SERVER_ID);

        try (NetClient client = new NetClient("POST", address)) {
            client.putHeaders(createHeaders(session, type, image, metadata));

            if (image != null) {
                client.putFile(type.toString().toLowerCase(Locale.US), "image/png", image);
            }

            String response = client.getResponseText();

            if (response.startsWith("ERROR: ")) { // lol @ "ERROR: OK"
                response = response.substring(7);
            }

            return new SkinUploadResponse(response.equalsIgnoreCase("OK"), response);
        }
    }


    protected Map<String, ?> createHeaders(Session session, Type type, URI image, Map<String, String> metadata) {
        Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("user", session.getUsername())
                .put("uuid", UUIDTypeAdapter.fromUUID(session.getProfile().getId()))
                .put("type", type.toString().toLowerCase(Locale.US));

        if (image == null) {
            builder.put("clear", "1");
        } else {
            builder.put("model", metadata.getOrDefault("mode", "default"));
        }

        return builder.build();
    }

    private static String getPath(String address, Type type, GameProfile profile) {
        String uuid = UUIDTypeAdapter.fromUUID(profile.getId());
        String path = type.toString().toLowerCase() + "s";

        return String.format("%s/%s/%s.png", address, path, uuid);
    }

    @Override
    protected ToStringBuilder addFields(ToStringBuilder builder) {
        return builder.append("address", address)
                      .append("gateway", gateway);
    }
}
