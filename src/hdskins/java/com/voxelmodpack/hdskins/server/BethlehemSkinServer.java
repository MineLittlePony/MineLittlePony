package com.voxelmodpack.hdskins.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.voxelmodpack.hdskins.gui.Feature;
import com.voxelmodpack.hdskins.util.IndentedToStringStyle;
import com.voxelmodpack.hdskins.util.MoreHttpResponses;
import com.voxelmodpack.hdskins.util.NetClient;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

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
            if (!response.ok()) {
                throw new IOException(response.getResponse().getStatusLine().getReasonPhrase());
            }

            return response.json(MinecraftTexturesPayload.class);
        }
    }

    @Override
    public SkinUploadResponse performSkinUpload(SkinUpload upload) throws IOException, AuthenticationException {
        SkinServer.verifyServerConnection(upload.getSession(), SERVER_ID);

        NetClient client = new NetClient("POST", address);

        client.putHeaders(createHeaders(upload));

        if (upload.getImage() != null) {
            client.putFile(upload.getType().toString().toLowerCase(Locale.US), "image/png", upload.getImage());
        }

        try (MoreHttpResponses response = client.send()) {
            if (!response.ok()) {
                throw new IOException(response.text());
            }
            return new SkinUploadResponse(response.text());
        }
    }

    protected Map<String, ?> createHeaders(SkinUpload upload) {
        Builder<String, Object> builder = ImmutableMap.<String, Object>builder()
                .put("accessToken", upload.getSession().getToken())
                .put("user", upload.getSession().getUsername())
                .put("uuid", UUIDTypeAdapter.fromUUID(upload.getSession().getProfile().getId()))
                .put("type", upload.getType().toString().toLowerCase(Locale.US));

        if (upload.getImage() == null) {
            builder.put("clear", "1");
        } else {
            builder.put("model", upload.getMetadata().getOrDefault("model", "default"));
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

    @Override
    public boolean supportsFeature(Feature feature) {
        switch (feature) {
            case DOWNLOAD_USER_SKIN:
            case UPLOAD_USER_SKIN:
            case MODEL_VARIANTS:
            case MODEL_TYPES:
            case LINK_PROFILE:
                return true;
            default: return false;
        }
    }
}
