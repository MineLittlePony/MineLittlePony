package com.minelittlepony.client.render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.google.common.base.Suppliers;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.*;
import net.minecraft.util.math.ColorHelper;

import java.util.function.*;

import com.minelittlepony.common.util.render.RenderLayerUtil;

public abstract class MagicGlow extends RenderPhase {
    private MagicGlow() {
        super(null, null, null);
    }

    private static final Supplier<RenderLayer> MAGIC = Suppliers.memoize(() -> {
        return RenderLayer.of("mlp_magic_glow",
                FabricLoader.getInstance().isModLoaded("vulkanmod") ? VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL : VertexFormats.POSITION_COLOR_LIGHT,
                VertexFormat.DrawMode.QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
            .program(EYES_PROGRAM)
            .writeMaskState(COLOR_MASK)
            .depthTest(LEQUAL_DEPTH_TEST)
            .transparency(LIGHTNING_TRANSPARENCY)
            .lightmap(DISABLE_LIGHTMAP)
            .cull(DISABLE_CULLING)
            .layering(VIEW_OFFSET_Z_LAYERING)
            .build(false));
    });

    private static final Function<Identifier, RenderLayer> TEXTURED = Util.memoize(texture -> {
        return RenderLayer.of("mlp_tint_layer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(texture, false, false))
                .program(EYES_PROGRAM)
                .writeMaskState(COLOR_MASK)
                .depthTest(LEQUAL_DEPTH_TEST)
                .transparency(LIGHTNING_TRANSPARENCY)
                .lightmap(DISABLE_LIGHTMAP)
                .cull(DISABLE_CULLING)
                .layering(VIEW_OFFSET_Z_LAYERING)
                .build(true));
    });

    public static RenderLayer getRenderLayer() {
        return MAGIC.get();
    }

    @Deprecated
    public static RenderLayer getColoured(Identifier texture, int color) {
        return TEXTURED.apply(texture);
    }

    public static RenderLayer getTextured(Identifier texture) {
        return TEXTURED.apply(texture);
    }

    @SuppressWarnings("deprecation")
    public static VertexConsumerProvider getProvider(int color, VertexConsumerProvider provider, MatrixStack matrices) {
        return layer -> {
            if (layer.getVertexFormat() != VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL) {
                return provider.getBuffer(layer);
            }

            return new MagicGlowOverlayVertexConsumer(provider.getBuffer(getTextured(RenderLayerUtil.getTexture(layer).orElse(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE))), matrices.peek(), color);
        };
    }

    public static void bootstrap() {}

    static class MagicGlowOverlayVertexConsumer extends OverlayVertexConsumer {
        private final VertexConsumer delegate;
        private final int color;

        public MagicGlowOverlayVertexConsumer(VertexConsumer delegate, Entry matrix, int color) {
            super(delegate, matrix, 1);
            this.delegate = delegate;
            this.color = color;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            delegate.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            delegate.color(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), alpha);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            delegate.normal(x, y, z);
            return this;
        }

        @Override
        public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
            delegate.vertex(x, y, z, ColorHelper.withAlpha(ColorHelper.getAlpha(color), this.color), u, v, overlay, light, normalX, normalY, normalZ);
        }

        // Sodium
        // https://github.com/CaffeineMC/sodium/blob/dev/common/src/main/java/net/caffeinemc/mods/sodium/mixin/core/render/immediate/consumer/SheetedDecalTextureGeneratorMixin.java
        // @Override
        public boolean canUseIntrinsics() {
            return false;
        }
    }
}
