package com.voxelmodpack.hdskins.server;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.util.NetClient;

import net.minecraft.util.Session;

@ServerType("bethlehem")
public class BethlehemSkinServer extends AbstractSkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    @Expose
    private final String address;

    private BethlehemSkinServer(String address) {
        this.address = address;
    }

    @Override
    public MinecraftTexturesPayload getProfileData(GameProfile profile) {
        try (NetClient client = new NetClient("GET", getPath(profile))) {
            if (!client.send()) {
                return null;
            }

            return gson.fromJson(client.getResponseText(), MinecraftTexturesPayload.class);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public SkinUploadResponse doUpload(Session session, URI image, Type type, Map<String, String> metadata) throws AuthenticationException, IOException {
        SkinServer.verifyServerConnection(session, SERVER_ID);

        try (NetClient client = new NetClient("POST", address)) {
            client.putHeaders(createHeaders(session, type, image, metadata));

            if (image != null) {
                client.putFile(type.toString().toLowerCase(Locale.US), "image/png", image);
            }

            return new SkinUploadResponse(client.send(), client.getResponseText());
        }
    }

    protected Map<String, ?> createHeaders(Session session, Type type, URI image, Map<String, String> metadata) {
        Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("accessToken", session.getToken())
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

    private String getPath(GameProfile profile) {
        return String.format("%s/profile/%s", address, UUIDTypeAdapter.fromUUID(profile.getId()));
    }

    @Override
    protected ToStringBuilder addFields(ToStringBuilder builder) {
        return builder.append("address", address);
    }
}
