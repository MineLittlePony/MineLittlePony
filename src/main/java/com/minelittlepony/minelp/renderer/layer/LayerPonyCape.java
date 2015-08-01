package com.minelittlepony.minelp.renderer.layer;

import com.minelittlepony.minelp.model.ModelPony;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class LayerPonyCape implements LayerRenderer {

    private RendererLivingEntity renderer;

    public LayerPonyCape(RendererLivingEntity entity) {
        renderer = entity;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ((ModelPony) renderer.getMainModel()).renderCloak((EntityPlayer) entitylivingbaseIn, partialTicks);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
