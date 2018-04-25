package com.minelittlepony.render.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

// separate class in case I need it later
public abstract class LayerOverlayBase<T extends EntityLiving> implements LayerRenderer<T> {

    protected final RenderLivingBase<?> renderer;

    public LayerOverlayBase(RenderLivingBase<?> render) {
        this.renderer = render;
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

    protected void renderOverlay(T skele, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ModelBase overlayModel = this.getOverlayModel();
        overlayModel.setModelAttributes(this.renderer.getMainModel());
        overlayModel.setLivingAnimations(skele, limbSwing, limbSwingAmount, partialTicks);
        renderer.bindTexture(this.getOverlayTexture());
        overlayModel.render(skele, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    protected abstract ModelBase getOverlayModel();

    protected abstract ResourceLocation getOverlayTexture();

}