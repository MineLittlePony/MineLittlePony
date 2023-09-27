package com.minelittlepony.client.render;

import net.minecraft.client.render.*;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

public class ArmorRenderLayers extends RenderPhase {
    private ArmorRenderLayers() {
        super(null, null, null);
    }

    private static final BiFunction<Identifier, Boolean, RenderLayer> ARMOR_TRANSLUCENT_NO_CULL = Util.memoize((texture, decal) -> {
        return RenderLayer.of(decal ? "armor_decal_translucent_no_cull" : "armor_translucent_no_cull",
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, MultiPhaseParameters.builder()
            .program(ARMOR_CUTOUT_NO_CULL_PROGRAM)
            .texture(new RenderPhase.Texture(texture, false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .cull(DISABLE_CULLING)
            .lightmap(ENABLE_LIGHTMAP)
            .overlay(ENABLE_OVERLAY_COLOR)
            .layering(VIEW_OFFSET_Z_LAYERING)
            .depthTest(decal ? EQUAL_DEPTH_TEST : LEQUAL_DEPTH_TEST)
            .build(true)
        );
    });

    public static RenderLayer getArmorTranslucentNoCull(Identifier texture, boolean decal) {
        return ARMOR_TRANSLUCENT_NO_CULL.apply(texture, decal);
    }
}
