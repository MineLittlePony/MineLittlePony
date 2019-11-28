package com.minelittlepony.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;

public class MagicGlow extends RenderPhase {

    public MagicGlow(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static RenderLayer getRenderLayer() {
        return RenderLayer.method_24048("mlp_magic_glow", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, 7, 256, RenderLayer.MultiPhaseData.builder()
                .texture(NO_TEXTURE)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .lightmap(DISABLE_LIGHTMAP)
                .cull(DISABLE_CULLING)
                .build(false));
    }
}
