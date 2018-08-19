package com.voxelmodpack.hdskins.skins;

import com.minelittlepony.avatar.texture.TextureType;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

public class SkinUpload {

    private URI image;
    private Map<String, String> metadata;
    private TextureType type;

    public SkinUpload(TextureType type, @Nullable URI image, Map<String, String> metadata) {
        this.image = image;
        this.metadata = metadata;
        this.type = type;
    }

    @Nullable
    public URI getImage() {
        return image;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public TextureType getType() {
        return type;
    }
}
