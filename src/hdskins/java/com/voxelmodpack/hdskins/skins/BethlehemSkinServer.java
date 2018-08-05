package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.util.Session;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

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
        try (MoreHttpResponses response = new NetClient("GET", getPath(profile)).send()) {

            JsonObject s = response.json(JsonObject.class);

            if (s.has("success") && s.get("success").getAsBoolean()) {
                s = s.get("data").getAsJsonObject();
                return gson.fromJson(s, MinecraftTexturesPayload.class);
            }
            throw new IOException(s.get("error").getAsString());
        }
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, SkinUpload skin) {
        URI image = skin.getImage();
        Map<String, String> metadata = skin.getMetadata();
        MinecraftProfileTexture.Type type = skin.getType();
        return CallableFutures.asyncFailableFuture(() -> {
            SkinServer.verifyServerConnection(session, SERVER_ID);

            NetClient client = new NetClient("POST", address).putHeaders(createHeaders(session, type, image, metadata));

            if (image != null) {
                client.putFile(type.toString().toLowerCase(Locale.US), "image/png", image);
            }

            try (MoreHttpResponses response = client.send()) {
                if (!response.ok()) {
                    throw new IOException(response.text());
                }
                return new SkinUploadResponse(response.text());
            }

        }, HDSkinManager.skinUploadExecutor);
    }

    protected Map<String, ?> createHeaders(Session session, Type type, @Nullable URI image, Map<String, String> metadata) {
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
    public String toString() {
        return new ToStringBuilder(this, IndentedToStringStyle.INSTANCE)
                .append("address", address)
                .build();
    }
}
