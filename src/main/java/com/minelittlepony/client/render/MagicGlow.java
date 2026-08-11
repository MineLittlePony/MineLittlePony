package com.minelittlepony.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;

import java.util.List;
import java.util.function.*;

import com.google.common.base.Suppliers;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.render.command.MagicOverlayOrderedRenderCommandQueue;
import com.minelittlepony.client.render.command.MagicOverlayRenderCommandQueue;
import com.minelittlepony.common.util.render.RenderLayerUtil;

public final class MagicGlow {
    public static final RenderPipeline /*EYES*/ ENTITY_MAGIC_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
                .withLocation("pipeline/magic_glow")
                .withVertexShader(MineLittlePony.id("core/magic"))
                .withFragmentShader(MineLittlePony.id("core/magic"))
                .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING)) /*changed from TRANSLUCENT */
                .withCull(false) /*added*/
                .withVertexBinding(0, DefaultVertexFormat.ENTITY)
                .withPrimitiveTopology(PrimitiveTopology.QUADS)
                .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                .build()
        );
    private static final Identifier NO_TEXTURE_ID = MineLittlePony.id("magic_solid");
    private static final Supplier<Identifier> EMPTY_TEXTURE = Suppliers.memoize(() -> {
        NativeImage image = new NativeImage(1, 1, false);
        image.setPixel(0, 0, CommonColors.WHITE);
        var texture = new DynamicTexture(() -> "Solid Color", image);
        texture.upload();
        Minecraft.getInstance().getTextureManager().register(NO_TEXTURE_ID, texture);
        return NO_TEXTURE_ID;
    });


    private static final BiFunction<Boolean, Identifier, RenderType> TEXTURED = Util.memoize((shaders, texture) -> {
        return RenderType.create("mlp_magic_glow_textured", RenderSetup.builder(shaders ? RenderPipelines.EYES : ENTITY_MAGIC_GLOW_PIPELINE)
            .withTexture("Sampler0", texture)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .setOutputTarget(OutputTarget.MAIN_TARGET)
            .setOutline(OutlineProperty.NONE)
            .createRenderSetup()
        );
    });

    public static RenderType getRenderLayer() {
        return getTextured(getDefaultTexture());
    }

    public static Identifier getDefaultTexture() {
        return EMPTY_TEXTURE.get();
    }

    public static RenderType getTextured(@Nullable Identifier texture) {
        if (texture == null) {
            texture = getDefaultTexture();
        }
        return TEXTURED.apply(IrisApiCompat.areShadersEnabled(), texture);
    }

    @SuppressWarnings("deprecation")
    public static SubmitNodeCollector getQueue(int color, SubmitNodeCollector frame, List<MagicOverlayRenderCommandQueue.Pass> passes) {
        if (frame instanceof MagicOverlayRenderCommandQueue p) {
            frame = p.unwrap();
        }
        return new MagicOverlayOrderedRenderCommandQueue(frame, layer -> {
            if (!layer.format().getElements().containsAll(DefaultVertexFormat.ENTITY.getElements())) {
                return null;
            }

            return getTextured(RenderLayerUtil.getTexture(layer).orElse(TextureAtlas.LOCATION_BLOCKS));
        }, color, passes);
    }

    public static void bootstrap() {}
}
