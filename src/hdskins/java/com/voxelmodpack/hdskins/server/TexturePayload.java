package com.voxelmodpack.hdskins.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class TexturePayload {

    private long timestamp;

    private UUID profileId;

    private String profileName;

    private boolean isPublic;

    private Map<String, MinecraftProfileTexture> textures;

    TexturePayload() { }

    public TexturePayload(GameProfile profile, Map<String, MinecraftProfileTexture> textures) {
        profileId = profile.getId();
        profileName = profile.getName();
        timestamp = System.currentTimeMillis();

        isPublic = true;

        this.textures = new HashMap<>(textures);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Map<String, MinecraftProfileTexture> getTextures() {
        return textures;
    }
}
