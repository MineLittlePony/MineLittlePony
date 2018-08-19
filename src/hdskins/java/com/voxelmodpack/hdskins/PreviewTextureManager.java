package com.voxelmodpack.hdskins;

import com.minelittlepony.avatar.texture.TextureData;
import com.minelittlepony.avatar.texture.TextureProfile;
import com.minelittlepony.avatar.texture.TextureType;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

/**
 * Manager for fetching preview textures. This ensures that multiple calls
 * to the skin server aren't done when fetching preview textures.
 */
public class PreviewTextureManager {

    private final Map<TextureType, TextureProfile> textures;

    PreviewTextureManager(Map<TextureType, TextureProfile> textures) {
        this.textures = textures;
    }

    @Nullable
    public PreviewTexture getPreviewTexture(ResourceLocation location, TextureType type, ResourceLocation def,
            @Nullable BiConsumer<TextureType, TextureData> callback) {
        if (!textures.containsKey(type)) {
            return null;
        }
        TextureProfile texture = textures.get(type);
        IImageBuffer buffer = new ImageBufferDownloadHD();
        PreviewTexture skinTexture = new PreviewTexture(texture, def,
                type == TextureType.SKIN ? new IImageBuffer() {
                    @Override
                    @Nullable
                    public BufferedImage parseUserSkin(BufferedImage image) {
                        return buffer.parseUserSkin(image);
                    }

                    @Override
                    public void skinAvailable() {
                        if (callback != null) {
                            callback.accept(type, new TextureData(location, texture));
                        }
                    }
                } : null);

        TextureLoader.loadTexture(location, skinTexture);

        return skinTexture;
    }

}
