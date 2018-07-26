package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.util.Session;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ServerType("bethlehem")
public class BethlehemSkinServer implements SkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    @Expose
    private final String address;

    private BethlehemSkinServer(String address) {
        this.address = address;
    }

    @Override
    public MinecraftTexturesPayload loadProfileData(GameProfile profile) throws IOException {
        // TODO: Fix this
        try (MoreHttpResponses response = new NetClient("GET", getPath(profile)).send()) {
            if (!response.ok()) {
                throw new IOException(response.getResponse().getStatusLine().getReasonPhrase());
            }

            return response.json(MinecraftTexturesPayload.class);
        }
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, SkinUpload upload) {
        return CallableFutures.asyncFailableFuture(() -> {
            SkinServer.verifyServerConnection(session, SERVER_ID);

            // TODO: Fix this
            NetClient client = new NetClient("POST", address);

            client.putHeaders(createHeaders(session, upload));

            if (upload.getImage() != null) {
                client.putFile(upload.getType().toString().toLowerCase(Locale.US), "image/png", upload.getImage());
            }

            try (MoreHttpResponses response = client.send()) {
                if (!response.ok()) {
                    throw new IOException(response.text());
                }
                return new SkinUploadResponse(response.text());
            }

        }, HDSkinManager.skinUploadExecutor);
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

    @Override
    public String toString() {
        return new IndentedToStringStyle.Builder(this)
                .append("address", address)
                .build();
    }
}
