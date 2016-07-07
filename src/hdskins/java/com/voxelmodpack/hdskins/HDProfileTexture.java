package com.voxelmodpack.hdskins;

import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

/**
 * Profile texture with a custom hash which is not the file name.
 */
public class HDProfileTexture extends MinecraftProfileTexture {

    private String hash;

    public HDProfileTexture(String url, @Nullable String hash, Map<String, String> metadata) {
        super(url, metadata);
        this.hash = hash;
    }

    @Override
    public String getHash() {
        return this.hash == null ? super.getHash() : this.hash;
    }

}
