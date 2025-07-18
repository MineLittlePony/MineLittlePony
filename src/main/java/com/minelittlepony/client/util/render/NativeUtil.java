package com.minelittlepony.client.util.render;

import com.minelittlepony.api.pony.meta.TriggerPixel;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.InputStream;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class NativeUtil {
    public static void parseImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail) {
        MinecraftClient.getInstance().execute(() -> {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.queueFencedTask(() -> _parseImage(resource, consumer, fail, 0));
                return;
            }
            _parseImage(resource, consumer, fail, 0);
        });
    }

    private static void _parseImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail,
                                    int attempt) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            TextureManager textures = mc.getTextureManager();

            AbstractTexture loadedTexture = textures.getTexture(resource);

            if (loadedTexture instanceof NativeImageBackedTexture nibt) {
                NativeImage image = nibt.getImage();
                if (image != null) {
                    consumer.accept(image::getColorArgb);
                    return;
                }
            }

            Resource res = mc.getResourceManager().getResource(resource).orElse(null);
            if (res != null) {
                try (InputStream inputStream = res.getInputStream()) {
                    try (NativeImage image = NativeImage.read(inputStream)) {
                        consumer.accept(image::getColorArgb);
                    }
                    return;
                }
            }

            __reconstructNativeImage(resource, consumer, fail, attempt);
        } catch (Exception e) {
            fail.accept(e);
        }
    }

    // Java
    private static void __reconstructNativeImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer,
                                                 Consumer<Exception> fail, int attempt) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Resource res = mc.getResourceManager().getResource(resource).orElse(null);
        if (res != null) {
            try (InputStream inputStream = res.getInputStream()) {
                try (NativeImage image = NativeImage.read(inputStream)) {
                    consumer.accept(image::getColorArgb);
                }
            } catch (Exception e) {
                fail.accept(e);
            }
        } else {
            fail.accept(new Exception("Resource not found: " + resource));
        }
    }
}
