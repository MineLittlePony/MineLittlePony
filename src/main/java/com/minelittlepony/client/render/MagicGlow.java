package com.minelittlepony.client.render;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.*;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.List;
import java.util.function.*;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.render.command.MagicOverlayOrderedRenderCommandQueue;
import com.minelittlepony.client.render.command.MagicOverlayRenderCommandQueue;
import com.minelittlepony.common.util.render.RenderLayerUtil;

public interface MagicGlow {
    RenderPipeline /*ENTITY_EYES*/ ENTITY_MAGIC_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET)
                .withLocation("pipeline/magic_glow")
                .withVertexShader(MineLittlePony.id("core/magic"))
                .withFragmentShader(MineLittlePony.id("core/magic"))
                .withSampler("Sampler0")
                .withBlend(BlendFunction.LIGHTNING)
                .withDepthWrite(false)
                .withCull(false) /*added*/
                .withDepthTestFunction(DepthTestFunction.LESS_DEPTH_TEST) /*added*/
                .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
                .build()
        );

    Function<Boolean, RenderLayer> MAGIC = Util.memoize(shaders -> {
        return RenderLayer.of("mlp_magic_glow", 1536, false, true, shaders ? RenderPipelines.ENTITY_EYES : ENTITY_MAGIC_GLOW_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.MAIN_TARGET)
                .build(false));
    });

    BiFunction<Boolean, Identifier, RenderLayer> TEXTURED = Util.memoize((shaders, texture) -> {
        return RenderLayer.of("mlp_magic_glow_textured", 1536, false, true, shaders ? RenderPipelines.ENTITY_EYES : ENTITY_MAGIC_GLOW_PIPELINE, RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(texture, false))
                .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.MAIN_TARGET)
                .build(false));
    });

    public static RenderLayer getRenderLayer() {
        return MAGIC.apply(IrisApiCompat.areShadersEnabled());
    }

    public static RenderLayer getTextured(Identifier texture) {
        return TEXTURED.apply(IrisApiCompat.areShadersEnabled(), texture);
    }

    @SuppressWarnings("deprecation")
    public static OrderedRenderCommandQueue getQueue(int color, OrderedRenderCommandQueue queue, List<MagicOverlayRenderCommandQueue.Pass> passes) {
        if (queue instanceof MagicOverlayRenderCommandQueue p) {
            queue = p.unwrap();
        }
        return new MagicOverlayOrderedRenderCommandQueue(queue, layer -> {
            if (!layer.getVertexFormat().getElements().containsAll(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL.getElements())) {
                return null;
            }

            return getTextured(RenderLayerUtil.getTexture(layer).orElse(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
        }, color, passes);
    }

    public static void bootstrap() {}
}
