package com.voxelmodpack.hdskins.server;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

public class SkinUpload {

    private URI image;
    private Map<String, String> metadata;
    private MinecraftProfileTexture.Type type;

    public SkinUpload(MinecraftProfileTexture.Type type, @Nullable URI image, Map<String, String> metadata) {
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

    public MinecraftProfileTexture.Type getType() {
        return type;
    }
}
