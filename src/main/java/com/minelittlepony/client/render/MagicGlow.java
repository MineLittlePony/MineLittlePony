package com.minelittlepony.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.render.RenderSetup.OutlineMode;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.*;
import net.minecraft.util.*;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.List;
import java.util.function.*;

import com.google.common.base.Suppliers;
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
    Identifier NO_TEXTURE_ID = MineLittlePony.id("magic_solid");
    Supplier<NativeImageBackedTexture> EMPTY_TEXTURE = Suppliers.memoize(() -> {
        NativeImage image = new NativeImage(1, 1, false);
        image.setColor(0, 0, Colors.WHITE);
        var texture = new NativeImageBackedTexture(() -> "Solid Color", image);
        texture.upload();
        return texture;
    });


    BiFunction<Boolean, Identifier, RenderLayer> TEXTURED = Util.memoize((shaders, texture) -> {
        @Nullable
        Supplier<GpuSampler> sampler = null;
        if (texture == null) {
            AbstractTexture resource = EMPTY_TEXTURE.get();
            MinecraftClient.getInstance().getTextureManager().registerTexture(NO_TEXTURE_ID, resource);
            sampler = resource::getSampler;
            texture = NO_TEXTURE_ID;
        }
        return RenderLayer.of("mlp_magic_glow_textured", RenderSetup.builder(shaders ? RenderPipelines.ENTITY_EYES : ENTITY_MAGIC_GLOW_PIPELINE)
            .texture("Sampler0", texture, sampler)
            .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .outputTarget(OutputTarget.MAIN_TARGET)
            .outlineMode(OutlineMode.NONE)
            .build()
        );
    });

    public static RenderLayer getRenderLayer() {
        return getTextured(null);
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
