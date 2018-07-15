package com.voxelmodpack.hdskins;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
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

    private IImageBuffer getPreviewImageBuffer(MinecraftProfileTexture texture, ResourceLocation location, Type type, SkinManager.SkinAvailableCallback callback) {
        if (type != Type.SKIN) {
            return null;
        }

        IImageBuffer buffer = new ImageBufferDownloadHD();

        return new IImageBuffer() {
            @Override
            @Nullable
            public BufferedImage parseUserSkin(BufferedImage image) {
                return buffer.parseUserSkin(image);
            }

            @Override
            public void skinAvailable() {
                if (callback != null) {
                    callback.skinAvailable(type, location, new MinecraftProfileTexture(texture.getUrl(), Maps.newHashMap()));
                }
            }
        };
    }

    @Nullable
    public PreviewTexture getPreviewTexture(ResourceLocation location, Type type, ResourceLocation def, @Nullable SkinManager.SkinAvailableCallback callback) {
        if (!textures.containsKey(type)) {
            return null;
        }

        MinecraftProfileTexture texture = textures.get(type);
        PreviewTexture skinTexture = new PreviewTexture(texture, def, getPreviewImageBuffer(texture, location, type, callback));

        Minecraft.getMinecraft().getTextureManager().loadTexture(location, skinTexture);

        return skinTexture;
    }
}
