package com.minelittlepony.client.render.layer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModel;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.components.ModelDeadMau5Ears;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.model.BodyPart;
import com.mojang.blaze3d.platform.GlStateManager;

public class LayerDJPon3Head<T extends AbstractClientPlayerEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    public LayerDJPon3Head(IPonyRender<T, M> context) {
        super(context);
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        if ("deadmau5".equals(entity.getName().getString())) {
            getContext().bindTexture(entity.getSkinTexture());

            GlStateManager.pushMatrix();
            getPlayerModel().transform(BodyPart.HEAD);
            getPlayerModel().getHead().applyTransform(scale);

            GlStateManager.scalef(1.3333334F, 1.3333334F, 1.3333334F);
            GlStateManager.translatef(0, 0.3F, 0);

            deadMau5.setVisible(true);
            deadMau5.render(move, swing, partialTicks, 0, 0, scale);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}
