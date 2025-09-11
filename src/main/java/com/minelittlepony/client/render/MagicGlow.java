package com.minelittlepony.client.render;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.*;
import net.minecraft.util.math.ColorHelper;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.google.common.base.Suppliers;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;

import java.util.function.*;

import com.minelittlepony.common.util.render.RenderLayerUtil;

public interface MagicGlow {
    RenderPipeline /*ENTITY_EYES*/ ENTITY_MAGIC_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_FOG_SNIPPET)
                .withLocation("pipeline/magic_glow")
                .withVertexShader("core/entity")
                .withFragmentShader("core/entity")
                .withShaderDefine("EMISSIVE")
                .withShaderDefine("NO_OVERLAY")
                .withShaderDefine("NO_CARDINAL_LIGHTING")
                .withSampler("Sampler0")
                .withBlend(BlendFunction.LIGHTNING)
                .withDepthWrite(false)
                .withCull(false) /*added*/
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST) /*added*/
                .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
                .build()
        );

    Supplier<RenderLayer> MAGIC = Suppliers.memoize(() -> {
        return RenderLayer.of("mlp_magic_glow", 1536, false, true, RenderPipelines.ENTITY_EYES, RenderLayer.MultiPhaseParameters.builder()
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.TRANSLUCENT_TARGET)
                .build(false));
    });

    Function<Identifier, RenderLayer> TINTED_LAYER = Util.memoize(texture -> {
        return RenderLayer.of("mlp_tint_layer", 1536, false, true, RenderPipelines.ENTITY_EYES, RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(texture, TriState.DEFAULT, false))
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.TRANSLUCENT_TARGET)
                .build(true));
    });

    public static RenderLayer getRenderLayer() {
        return MAGIC.get();
    }

    public static RenderLayer getTextured(Identifier texture) {
        return TINTED_LAYER.apply(texture);
    }

    @SuppressWarnings("deprecation")
    public static VertexConsumerProvider getProvider(int color, VertexConsumerProvider provider, MatrixStack matrices) {
        return layer -> {
            if (!layer.getVertexFormat().getElements().containsAll(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL.getElements())) {
                return new DummyVertexConsumer();
            }

            return new MagicGlowOverlayVertexConsumer(provider.getBuffer(getTextured(RenderLayerUtil.getTexture(layer).orElse(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE))), matrices.peek(), color);
        };
    }

    public static void bootstrap() {}

    static class DummyVertexConsumer implements VertexConsumer {
        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            return this;
        }

        // Sodium
        // https://github.com/CaffeineMC/sodium/blob/dev/common/src/main/java/net/caffeinemc/mods/sodium/mixin/core/render/immediate/consumer/SheetedDecalTextureGeneratorMixin.java
        // @Override
        public boolean canUseIntrinsics() {
            return false;
        }
    }

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
