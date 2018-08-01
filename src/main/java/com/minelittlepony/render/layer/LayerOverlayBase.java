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
        renderer = render;
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

    @Override
    public void doRenderLayer(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        ModelBase overlayModel = getOverlayModel();

        overlayModel.setModelAttributes(renderer.getMainModel());
        overlayModel.setLivingAnimations(entity, move, swing, partialTicks);
        overlayModel.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        renderer.bindTexture(getOverlayTexture());

        overlayModel.render(entity, move, swing, ticks, headYaw, headPitch, scale);
    }

    protected abstract ModelBase getOverlayModel();

    protected abstract ResourceLocation getOverlayTexture();

}
