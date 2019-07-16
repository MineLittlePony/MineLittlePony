package com.minelittlepony.client.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.mojang.blaze3d.platform.GlStateManager.getTexLevelParameter;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_INTERNAL_FORMAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;

public class NativeUtil {
    private static final NativeImage.Format[] formats = NativeImage.Format.values();

    public static NativeImage.Format getFormat(int glFormat) {
        for (NativeImage.Format i : formats) {
            if (i.getPixelDataFormat() == glFormat) {
                return i;
            }
        }

        throw new RuntimeException("Unsupported image format");
    }

    public static <T> T parseImage(Identifier resource, Function<NativeImage, T> consumer) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TextureManager textures = mc.getTextureManager();

        if (!mc.isOnThread()) {
            throw new IllegalStateException("This can only be called from the main thread.");
        }

        // recreate NativeImage from the GL matrix
        textures.bindTexture(resource);

        int format = getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        int width  = getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        int height = getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

        if (width * height == 0) {
            throw new IllegalStateException("GL texture not uploaded yet");
        }

        try (NativeImage image = new NativeImage(getFormat(format), width, height, false)) {
            // This allocates a new array to store the image every time.
            // Don't do this every time. Keep a cache and store it so we don't destroy memory.
            image.loadFromTextureImage(0, false);

            return consumer.apply(image);
        }
    }

}
