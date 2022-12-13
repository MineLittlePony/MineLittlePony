package com.minelittlepony.client.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mojang.blaze3d.platform.GlStateManager._getTexLevelParameter;
import static org.lwjgl.opengl.GL11.*;

public class NativeUtil {
    enum InternalFormat {
        RGB(NativeImage.Format.RGB),
        RGBA(NativeImage.Format.RGBA),
        LUMINANCE_ALPHA(NativeImage.Format.LUMINANCE_ALPHA),
        LUMINANCE(NativeImage.Format.LUMINANCE),

        ALPHA4(GL_ALPHA4, NativeImage.Format.LUMINANCE_ALPHA),
        ALPHA8(GL_ALPHA8, NativeImage.Format.LUMINANCE_ALPHA),
        ALPHA12(GL_ALPHA12, NativeImage.Format.LUMINANCE_ALPHA),
        ALPHA16(GL_ALPHA16, NativeImage.Format.LUMINANCE_ALPHA),

        LUMINANCE4(GL_LUMINANCE4, NativeImage.Format.LUMINANCE),
        LUMINANCE8(GL_LUMINANCE8, NativeImage.Format.LUMINANCE),
        LUMINANCE12(GL_LUMINANCE12, NativeImage.Format.LUMINANCE),
        LUMINANCE16(GL_LUMINANCE16, NativeImage.Format.LUMINANCE),
        LUMINANCE4_ALPHA4(GL_LUMINANCE4_ALPHA4, NativeImage.Format.LUMINANCE_ALPHA),
        LUMINANCE6_ALPHA2(GL_LUMINANCE6_ALPHA2, NativeImage.Format.LUMINANCE_ALPHA),
        LUMINANCE8_ALPHA8(GL_LUMINANCE8_ALPHA8, NativeImage.Format.LUMINANCE_ALPHA),
        LUMINANCE12_ALPHA4(GL_LUMINANCE12_ALPHA4, NativeImage.Format.LUMINANCE_ALPHA),
        LUMINANCE12_ALPHA12(GL_LUMINANCE12_ALPHA12, NativeImage.Format.LUMINANCE_ALPHA),
        LUMINANCE16_ALPHA16(GL_LUMINANCE16_ALPHA16, NativeImage.Format.LUMINANCE_ALPHA),

        INTENSITY(GL_INTENSITY, NativeImage.Format.LUMINANCE),
        INTENSITY4(GL_INTENSITY4, NativeImage.Format.LUMINANCE),
        INTENSITY8(GL_INTENSITY8, NativeImage.Format.LUMINANCE),
        INTENSITY12(GL_INTENSITY12, NativeImage.Format.LUMINANCE),
        INTENSITY16(GL_INTENSITY16, NativeImage.Format.LUMINANCE),

        R3_G3_B2(GL_R3_G3_B2, NativeImage.Format.RGB),
        RGB4(GL_RGB4, NativeImage.Format.RGB),
        RGB5(GL_RGB5, NativeImage.Format.RGB),
        RGB8(GL_RGB8, NativeImage.Format.RGB),
        RGB10(GL_RGB10, NativeImage.Format.RGB),
        RGB12(GL_RGB12, NativeImage.Format.RGB),
        RGB16(GL_RGB16, NativeImage.Format.RGB),

        RGBA2(GL_RGBA2, NativeImage.Format.RGBA),
        RGBA4(GL_RGBA4, NativeImage.Format.RGBA),
        RGB5_A1(GL_RGB5_A1, NativeImage.Format.RGBA),
        RGBA8(GL_RGBA8, NativeImage.Format.RGBA),
        RGB10_A2(GL_RGB10_A2, NativeImage.Format.RGBA),
        RGBA12(GL_RGBA12, NativeImage.Format.RGBA),
        RGBA16(GL_RGBA16, NativeImage.Format.RGBA);

        private final NativeImage.Format formatClass;
        private final int glId;

        public static Map<Integer, InternalFormat> LOOKUP = new HashMap<>();

        private InternalFormat(NativeImage.Format formatClass) {
            this(formatClass.toGl(), formatClass);
        }

        private InternalFormat(int glId, NativeImage.Format formatClass) {
            this.glId = glId;
            this.formatClass = formatClass;
        }

        public NativeImage.Format getClassification() {
            return formatClass;
        }

        public static InternalFormat valueOf(int glId) {
            if (!LOOKUP.containsKey(glId)) {
                throw new IllegalStateException("Unsupported image format: " + glId);
            }
            return LOOKUP.get(glId);
        }

        static {
            for (InternalFormat f : values()) {
                LOOKUP.put(f.glId, f);
            }
        }
    }

    public static void parseImage(Identifier resource, Consumer<NativeImage> consumer, Consumer<Exception> fail) {
        parseImage(resource, consumer, fail, 0);
    }

    private static void parseImage(Identifier resource, Consumer<NativeImage> consumer, Consumer<Exception> fail, int attempt) {
        try {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> parseImage(resource, consumer, fail, attempt));
                return;
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            TextureManager textures = mc.getTextureManager();

            // recreate NativeImage from the GL matrix
            textures.bindTexture(resource);

                                                     // TODO: This returns values that are too specific.
                                                     //       Can we change the level (0) here to something
                                                     //       else to actually get what we need?
            int format = _getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
            int width  = _getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
            int height = _getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

            if (width * height == 0) {
                if (attempt < 3) {
                    CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS, mc).execute(() -> {
                        parseImage(resource, consumer, fail, attempt + 1);
                    });
                } else {
                    throw new IllegalStateException("GL texture not uploaded yet: " + resource + " Parse failed 3/3 attempts");
                }
            }

            try (NativeImage image = new NativeImage(InternalFormat.valueOf(format).getClassification(), width, height, false)) {
                // This allocates a new array to store the image every time.
                // Don't do this every time. Keep a cache and store it so we don't destroy memory.
                image.loadFromTextureImage(0, false);
                consumer.accept(image);
            }
        } catch (Exception e) {
            fail.accept(e);
        }
    }
}
