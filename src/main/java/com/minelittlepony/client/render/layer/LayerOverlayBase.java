package com.minelittlepony.client.render.layer;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.model.IModel;

// separate class in case I need it later
public abstract class LayerOverlayBase<T extends LivingEntity, M extends BipedEntityModel<T> & IModel> extends FeatureRenderer<T, M> {

    protected final FeatureRendererContext<T, M> renderer;

    public LayerOverlayBase(FeatureRendererContext<T, M> render) {
        super(render);
        renderer = render;
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        M overlayModel = getOverlayModel();

        renderer.getModel().setAttributes(overlayModel);
        overlayModel.animateModel(entity, move, swing, partialTicks);
        overlayModel.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        renderer.bindTexture(getOverlayTexture());

        overlayModel.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);
    }

    protected abstract M getOverlayModel();

    protected abstract Identifier getOverlayTexture();

}