package com.voxelmodpack.hdskins.skins;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.util.Session;

@ServerType("bethlehem")
public class BethlehemSkinServer extends AbstractSkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    public BethlehemSkinServer(String address) {
        super(address);
    }

    @Override
    public MinecraftTexturesPayload getProfileData(GameProfile profile) throws IOException {
        try (NetClient client = new NetClient("GET", getPath(profile))) {
            if (client.getResponse().ok()) {
                return client.getResponse().json(MinecraftTexturesPayload.class);
            }
        }
        return null;
    }

    @Override
    protected SkinUploadResponse doUpload(Session session, SkinUpload upload) throws AuthenticationException, IOException {
        SkinServer.verifyServerConnection(session, SERVER_ID);

        try (NetClient client = new NetClient("POST", address)) {
            client.putHeaders(createHeaders(session, upload));

            if (upload.getImage() != null) {
                client.putFile(upload.getType().toString().toLowerCase(Locale.US), "image/png", upload.getImage());
            }

            if (client.getResponse().ok()) {
                return new SkinUploadResponse(client.getResponse().text());
            }

            throw new IOException(client.getResponse().text());
        }
    }

    protected Map<String, ?> createHeaders(Session session, SkinUpload upload) {
        Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("accessToken", session.getToken())
                .put("user", session.getUsername())
                .put("uuid", UUIDTypeAdapter.fromUUID(session.getProfile().getId()))
                .put("type", upload.getType().toString().toLowerCase(Locale.US));

        if (upload.getImage() == null) {
            builder.put("clear", "1");
        } else {
            builder.put("model", upload.getMetadata().getOrDefault("mode", "default"));
        }

        return builder.build();
    }

    private String getPath(GameProfile profile) {
        return String.format("%s/profile/%s", address, UUIDTypeAdapter.fromUUID(profile.getId()));
    }
}
