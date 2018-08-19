package com.voxelmodpack.hdskins;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

/**
 * Manager for fetching preview textures. This ensures that multiple calls
 * to the skin server aren't done when fetching preview textures.
 */
public class PreviewTextureManager {

    private final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;

    private PreviewTextureManager(MinecraftTexturesPayload payload) {
        this.textures = payload.getTextures();
    }

    @Nullable
    public PreviewTexture getPreviewTexture(ResourceLocation location, MinecraftProfileTexture.Type type, ResourceLocation def,
            @Nullable SkinManager.SkinAvailableCallback callback) {
        if (!textures.containsKey(type)) {
            return null;
        }
        MinecraftProfileTexture texture = textures.get(type);
        IImageBuffer buffer = new ImageBufferDownloadHD();
        PreviewTexture skinTexture = new PreviewTexture(texture.getMetadata("model"), texture.getUrl(), def,
                type == MinecraftProfileTexture.Type.SKIN ? new IImageBuffer() {
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
                } : null);

        TextureLoader.loadTexture(location, skinTexture);

        return skinTexture;
    }

    public static CompletableFuture<PreviewTextureManager> load(SkinServer server, GameProfile profile) {
        return server.getPreviewTextures(profile).thenApply(PreviewTextureManager::new);
    }
}
