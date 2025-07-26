package com.minelittlepony.client.render;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.*;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.function.*;

import com.google.common.base.Suppliers;
import com.minelittlepony.common.util.render.RenderLayerUtil;

public interface MagicGlow {
    RenderPipeline /*ENTITY_EYES*/ ENTITY_MAGIC_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET)
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
        return RenderLayer.of("mlp_magic_glow", 1536, false, true, ENTITY_MAGIC_GLOW_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.TRANSLUCENT_TARGET)
                .build(false));
    });

    Function<Identifier, RenderLayer> TEXTURED = Util.memoize(texture -> {
        return RenderLayer.of("mlp_magic_glow_textured", 1536, false, true, ENTITY_MAGIC_GLOW_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(texture, false))
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.TRANSLUCENT_TARGET)
                .build(true));
    });

    public static RenderLayer getRenderLayer() {
        return MAGIC.get();
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
            delegate.color(color);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            this.delegate.normal(x, y, z);
            return this;
        }
    }
}
