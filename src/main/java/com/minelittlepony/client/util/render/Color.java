package com.minelittlepony.client.util.render;

import org.lwjgl.opengl.GL14;

import com.mojang.blaze3d.platform.GlStateManager;

/**
 * Colouration Utilities
 */
public interface Color {

    /**
     * Returns the ALPHA channel for the given colour hex code.
     */
    static float a(int hex) {
        return (hex >> 24 & 255) / 255F;
    }

    /**
     * Returns the RED channel for the given colour hex code.
     */
    static float r(int hex) {
        return (hex >> 16 & 255) / 255F;
    }

    /**
     * Returns the GREEN channel for the given colour hex code.
     */
    static float g(int hex) {
        return (hex >> 8 & 255) / 255F;
    }

    /**
     * Returns the BLUE channel for the given colour hex code.
     */
    static float b(int hex) {
        return (hex & 255) / 255F;
    }

    /**
     * Converts the given rgb floats on a range of 0-1 into a colour hex code.
     */
    static int argbToHex(float a, float r, float g, float b) {
        return argbToHex((int)(a * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    /**
     * Converts the given rbg int on a range of 0-255 into a colour hex code.
     */
    static int argbToHex(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | (b);
    }

    /**
     * Converts a colour hex code from BGR to RGB (and back).
     */
    static int abgrToArgb(int color) {
        return argbToHex(a(color), b(color), g(color), r(color));
    }

    /**
     * Applies a GLTint based on the given colour hex code.
     *
     * @param hex The colour to apply
     */
    static void glColor(int hex) {
        glColor(hex, a(hex));
    }

    /**
     * Applies a GLTint based on the given colour hex code.
     *
     * @param hex The colour to apply
     * @param alpha The opacity to use
     */
    static void glColor(int hex, float alpha) {
        GlStateManager.color4f(r(hex), g(hex), b(hex), alpha);
    }

    /**
     * Applies a GLBlendTint based on the given colour hex code.
     *
     * @param hex The colour to apply
     */
    static void glBlendColour(int hex) {
        glBlendColour(hex, a(hex));
    }

    /**
     * Applies a GLBlendTint based on the given colour hex code.
     *
     * @param hex The colour to apply
     * @param alpha The opacity to use
     */
    static void glBlendColour(int hex, float alpha) {
        GL14.glBlendColor(r(hex), g(hex), b(hex), alpha);
    }
}
