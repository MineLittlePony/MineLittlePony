package com.voxelmodpack.hdskins.skins;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.util.Session;

@ServerType("bethlehem")
public class BethlehemSkinServer implements SkinServer {

    private static final String SERVER_ID = "7853dfddc358333843ad55a2c7485c4aa0380a51";

    @Expose
    private final String address;

    private BethlehemSkinServer(String address) {
        this.address = address;
    }

    @Override
    public Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile) {
        NetClient client = new NetClient("GET", getPath(profile));

        String json = client.getResponseText();

        JsonObject s = gson.fromJson(json, JsonObject.class);

        if (s.has("success") && s.get("success").getAsBoolean()) {
            s = s.get("data").getAsJsonObject();

            return Optional.ofNullable(gson.fromJson(s, MinecraftTexturesPayload.class));
        }

        return Optional.empty();
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, URI image, Type type, Map<String, String> metadata) {
        return CallableFutures.asyncFailableFuture(() -> {
            SkinServer.verifyServerConnection(session, SERVER_ID);

            NetClient client = new NetClient("POST", address).putHeaders(createHeaders(session, type, image, metadata));

            if (image != null) {
                client.putFile(type.toString().toLowerCase(Locale.US), "image/png", image);
            }

            return new SkinUploadResponse(client.send(), client.getResponseText());
        }, HDSkinManager.skinUploadExecutor);
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
    public String toString() {
        return new ToStringBuilder(this, IndentedToStringStyle.INSTANCE)
                .append("address", address)
                .build();
    }
}
