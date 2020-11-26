package com.minelittlepony.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

public class MagicGlow extends RenderPhase {
    private MagicGlow(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    protected static final RenderPhase.Transparency GLOWING_TRANSPARENCY = new RenderPhase.Transparency("glowing_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                SrcFactor.CONSTANT_COLOR, DstFactor.ONE,
                SrcFactor.ONE, DstFactor.ZERO);
     }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
     });

    private static final RenderLayer MAGIC = RenderLayer.of("mlp_magic_glow", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
            .texture(NO_TEXTURE)
            .writeMaskState(COLOR_MASK)
            .transparency(LIGHTNING_TRANSPARENCY)
            .lightmap(DISABLE_LIGHTMAP)
            .cull(DISABLE_CULLING)
            .build(false));

    public static RenderLayer getRenderLayer() {
        return MAGIC;
    }

    public static RenderLayer getTintedTexturedLayer(Identifier texture, float red, float green, float blue, float alpha) {
        return RenderLayer.of("mlp_tint_layer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
                .texture(new Color(texture, red, green, blue, alpha))
                .writeMaskState(COLOR_MASK)
                .alpha(ONE_TENTH_ALPHA)
                .transparency(GLOWING_TRANSPARENCY)
                .lightmap(DISABLE_LIGHTMAP)
                .overlay(DISABLE_OVERLAY_COLOR)
                .cull(DISABLE_CULLING)
                .build(true));
    }

    private static class Color extends Texture {

        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        public Color(Identifier texture, float red, float green, float blue, float alpha) {
            super(texture, false, false);
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public void startDrawing() {
            RenderSystem.blendColor(red, green, blue, alpha);
            super.startDrawing();
        }

        @Override
        public void endDrawing() {
            super.endDrawing();
            RenderSystem.blendColor(1, 1, 1, 1);
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other)
                    && ((Color)other).red == red
                    && ((Color)other).green == green
                    && ((Color)other).blue == blue
                    && ((Color)other).alpha == alpha;
        }
    }
}
