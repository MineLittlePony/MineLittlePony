package com.minelittlepony.client.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import com.minelittlepony.api.pony.meta.TriggerPixel;
import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;

import java.io.InputStream;
import java.util.function.Consumer;

public class NativeUtil {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static void parseImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail) {
        CLIENT.execute(() -> {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.queueFencedTask(() -> _parseImage(resource, consumer, fail, 0));
                return;
            }
            _parseImage(resource, consumer, fail, 0);
        });
    }

    private static void _parseImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail, int attempt) {
        try {
            TextureManager textures = CLIENT.getTextureManager();
            AbstractTexture loadedTexture = textures.getTexture(resource);

            if (loadedTexture instanceof DynamicTexture nibt) {
                NativeImage image = nibt.getPixels();
                if (image != null) {
                    consumer.accept(image::getPixel);
                    return;
                }
            }

            Resource res = CLIENT.getResourceManager().getResource(resource).orElse(null);
            if (res != null) {
                try (InputStream inputStream = res.open()) {
                    try (NativeImage image = NativeImage.read(inputStream)) {
                        consumer.accept(image::getPixel);
                    }
                    return;
                }
            }

            __reconstructNativeImage(loadedTexture, consumer, fail, attempt);
        } catch (Exception e) {
            fail.accept(e);
        }
    }

    private static void __reconstructNativeImage(AbstractTexture loadedTexture, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail, int attempt) {
        GpuTexture texture = loadedTexture.getTexture();

        int format = texture.getFormat().blockSize();
        int width  = texture.getWidth(0);
        int height = texture.getHeight(0);

        try (GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Texture Retrieval buffer", 9, width * height * format)) {
            RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(texture, gpuBuffer, 0, () -> {
                try (MappedView readView = gpuBuffer.map(true, false)) {
                    var data = readView.data();
                    consumer.accept((x, y) -> data.getInt((x + (height - y) * width) * format));
                }
            }, 0);
        }
    }
}
