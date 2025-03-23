package com.minelittlepony.client.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.TriggerPixel;
import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;

import java.io.InputStream;
import java.util.function.Consumer;

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

    private static void _parseImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail, int attempt) {
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

    private static void __reconstructNativeImage(Identifier resource, Consumer<TriggerPixel.Mat> consumer, Consumer<Exception> fail, int attempt) {
        MinecraftClient mc = MinecraftClient.getInstance();

        GpuTexture texture = mc.getTextureManager().getTexture(resource).getGlTexture();

        int format = texture.getFormat().pixelSize();
        int width  = texture.getWidth(0);
        int height = texture.getHeight(0);

        try (GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Texture Retrieval buffer", BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, width * height * format)) {
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(texture, gpuBuffer, 0, () -> {
                try (GpuBuffer.ReadView readView = commandEncoder.readBuffer(gpuBuffer)) {
                    try (NativeImage image = new NativeImage(width, height, false)) {
                        for (int k = 0; k < height; k++) {
                            for (int l = 0; l < width; l++) {
                                int m = readView.data().getInt((l + k * width) * format);
                                image.setColor(l, height - k - 1, m);
                            }
                        }

                        consumer.accept(image::getColorArgb);
                    }
                }
            }, 0);
        }
    }
}
