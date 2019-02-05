package com.minelittlepony.util.render;

import net.minecraft.client.renderer.GlStateManager;

/**
 * Colouration Utilities
 */
public interface Color {
    /**
     * Returns the RED channel for the given colour integer.
     */
    static float r(int color) {
        return (color >> 16 & 255) / 255F;
    }

    /**
     * Returns the GREEN channel for the given colour integer.
     */
    static float g(int color) {
        return (color >> 8 & 255) / 255F;
    }

    /**
     * Returns the BLUE channel for the given colour integer.
     */
    static float b(int color) {
        return (color & 255) / 255F;
    }

    /**
     * Converts the given rgb floats on a range of 0-1 into a minecraft colour integer.
     */
    static int colorInteger(float r, float g, float b) {
        return colorInteger((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    /**
     * Converts the given rbg int on a range of 0-255 into a minecraft colour integer.
     */
    static int colorInteger(int r, int g, int b) {
        return (r << 16) | (g << 8) | (b);
    }

    /**
     * Applies a GLTint based on the given colour integer.
     *
     * @param color The colour to apply
     * @param alpha The opacity to use
     */
    static void glColor(int color, float alpha) {
        GlStateManager.color(r(color), g(color), b(color), alpha);
    }
}
