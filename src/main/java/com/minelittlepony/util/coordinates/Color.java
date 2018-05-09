package com.minelittlepony.util.coordinates;

import net.minecraft.client.renderer.GlStateManager;

public interface Color {
    public static float r(int color) {
        return (color >> 16 & 255) / 255F;
    }

    public static float g(int color) {
        return (color >> 8 & 255) / 255F;
    }

    public static float b(int color) {
        return (color & 255) / 255F;
    }

    public static void glColor(int color, float alpha) {
        GlStateManager.color(Color.r(color), Color.g(color), Color.b(color), alpha);
    }
}
