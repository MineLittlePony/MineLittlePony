package com.minelittlepony.minelp.renderer.layer;

import com.minelittlepony.minelp.model.ModelPony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public class LayerHeldPonyItem implements LayerRenderer {

    private final RendererLivingEntity livingPonyEntity;

    public LayerHeldPonyItem(RendererLivingEntity livingPony) {
        this.livingPonyEntity = livingPony;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
            float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ModelPony pony = (ModelPony) livingPonyEntity.getMainModel();
        Minecraft mc = Minecraft.getMinecraft();
        pony.renderDrop(mc.getRenderManager(), mc.getItemRenderer(), entitylivingbaseIn);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
