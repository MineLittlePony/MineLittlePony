package com.voxelmodpack.hdskins;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Manager for fetching preview textures. This ensures that multiple calls
 * to the skin server aren't done when fetching preview textures.
 */
public class PreviewTextureManager {

    private final Map<Type, MinecraftProfileTexture> textures;

    PreviewTextureManager(Map<Type, MinecraftProfileTexture> textures) {
        this.textures = textures;
    }

    @Nullable
    public PreviewTexture getPreviewTexture(ResourceLocation location, Type type, ResourceLocation def, @Nullable SkinAvailableCallback callback) {
        if (!textures.containsKey(type)) {
            return null;
        }

        MinecraftProfileTexture texture = textures.get(type);

        IImageBuffer buffer = type != Type.SKIN ? null : new ImageBufferDownloadHD().withCallback(() -> {
            if (callback != null) {
                callback.skinAvailable(type, location, new MinecraftProfileTexture(texture.getUrl(), Maps.newHashMap()));
            }
        });

        PreviewTexture skinTexture = new PreviewTexture(texture, def, buffer);

        Minecraft.getMinecraft().getTextureManager().loadTexture(location, skinTexture);

        return skinTexture;
    }
}
