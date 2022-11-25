package com.voxelmodpack.hdskins.resources;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.resources.texture.ISkinAvailableCallback;
import com.voxelmodpack.hdskins.resources.texture.ImageBufferDownloadHD;
import com.voxelmodpack.hdskins.server.TexturePayload;

import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Manager for fetching preview textures. This ensures that multiple calls
 * to the skin server aren't done when fetching preview textures.
 */
public class PreviewTextureManager {

    private final Map<String, MinecraftProfileTexture> textures;

    public PreviewTextureManager(TexturePayload payload) {
        this.textures = payload.getTextures();
    }

    @Nullable
    public PreviewTexture getPreviewTexture(ResourceLocation location, MinecraftProfileTexture.Type type, ResourceLocation def, @Nullable SkinManager.SkinAvailableCallback callback) {

        String key = type.name().toLowerCase(Locale.ROOT);

        if (!textures.containsKey(key)) {
            return null;
        }

        MinecraftProfileTexture texture = textures.get(key);
        ISkinAvailableCallback buff = new ImageBufferDownloadHD(type, () -> {
            if (callback != null) {
                callback.skinAvailable(type, location, new MinecraftProfileTexture(texture.getUrl(), Maps.newHashMap()));
            }
        });

        PreviewTexture skinTexture = new PreviewTexture(texture.getMetadata("model"), texture.getUrl(), def, buff);

        TextureLoader.loadTexture(location, skinTexture);

        return skinTexture;
    }
}
