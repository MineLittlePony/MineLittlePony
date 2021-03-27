package com.minelittlepony.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.function.BiFunction;

public abstract class MagicGlow extends RenderPhase {
    private MagicGlow(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    private static final RenderLayer MAGIC = RenderLayer.of("mlp_magic_glow", VertexFormats.POSITION_COLOR_LIGHT, VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
            .method_34578/*shader*/(field_29414/*EYES*/)
            .writeMaskState(COLOR_MASK)
            .depthTest(LEQUAL_DEPTH_TEST)
            .transparency(LIGHTNING_TRANSPARENCY)
            .lightmap(DISABLE_LIGHTMAP)
            .cull(DISABLE_CULLING)
            .build(false));

    private static final BiFunction<Identifier, Integer, RenderLayer> TINTED_LAYER = Util.memoize((texture, color) -> {
        return RenderLayer.of("mlp_tint_layer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
                .method_34577/*texture*/(new Color(texture, color))
                .method_34578/*shader*/(field_29414/*EYES*/)
                .writeMaskState(COLOR_MASK)
                .depthTest(LEQUAL_DEPTH_TEST)
                .transparency(LIGHTNING_TRANSPARENCY)
                .lightmap(DISABLE_LIGHTMAP)
                .cull(DISABLE_CULLING)
                .build(true));
    });

    public static RenderLayer getRenderLayer() {
        return MAGIC;
    }

    public static RenderLayer getTintedTexturedLayer(Identifier texture, float red, float green, float blue, float alpha) {
        return TINTED_LAYER.apply(texture, com.minelittlepony.common.util.Color.argbToHex(alpha, red, green, blue));
    }

    private static class Color extends Texture {

        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        public Color(Identifier texture, int color) {
            super(texture, false, false);
            this.red = com.minelittlepony.common.util.Color.r(color);
            this.green = com.minelittlepony.common.util.Color.g(color);
            this.blue = com.minelittlepony.common.util.Color.b(color);
            this.alpha = com.minelittlepony.common.util.Color.a(color);
        }

        @Override
        public void startDrawing() {
            RenderSystem.setShaderColor(red, green, blue, alpha);
            super.startDrawing();
        }

        @Override
        public void endDrawing() {
            super.endDrawing();
            RenderSystem.setShaderColor(1, 1, 1, 1);
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
