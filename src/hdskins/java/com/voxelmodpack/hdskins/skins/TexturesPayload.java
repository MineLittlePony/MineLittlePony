package com.voxelmodpack.hdskins.skins;

import com.minelittlepony.avatar.texture.TextureProfile;
import com.minelittlepony.avatar.texture.TextureType;
import com.mojang.authlib.GameProfile;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class TexturesPayload {

    private long timestamp;
    private UUID profileId;
    private String profileName;
    private boolean isPublic;
    private Map<TextureType, TextureProfile> textures;

    public TexturesPayload(GameProfile profile, Map<TextureType, TextureProfile> textures) {
        this.profileId = profile.getId();
        this.profileName = profile.getName();
        this.timestamp = System.currentTimeMillis();

        this.isPublic = true;

        this.textures = textures;
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

    @Nullable
    public Map<TextureType, TextureProfile> getTextures() {
        return textures;
    }

}
