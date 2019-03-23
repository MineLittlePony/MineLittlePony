package com.minelittlepony.hdskins.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.renderer.texture.DynamicTexture;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.Field;
import java.util.Map;

public class ProfileTextureUtil {

    private static Field metadata = FieldUtils.getDeclaredField(MinecraftProfileTexture.class, "metadata", true);

    @SuppressWarnings("unchecked")
    public static Map<String, String> getMetadata(MinecraftProfileTexture texture) {
        try {
            return (Map<String, String>) FieldUtils.readField(metadata, texture);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to read metadata field", e);
        }
    }

    public static void setMetadata(MinecraftProfileTexture texture, Map<String, String> meta) {
        try {
            FieldUtils.writeField(metadata, texture, meta);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to write metadata field", e);
        }
    }

    public static BufferedImage getDynamicBufferedImage(int width, int height, DynamicTexture texture) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        final int[] src = texture.getTextureData();

        final int[] dst = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        System.arraycopy(src, 0, dst, 0, src.length);

        return image;
    }
}
