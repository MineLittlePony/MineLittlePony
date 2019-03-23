package com.minelittlepony.render.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.components.ModelDeadMau5Ears;

public class LayerDJPon3Head extends AbstractPonyLayer<AbstractClientPlayer> {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    public LayerDJPon3Head(RenderLivingBase<AbstractClientPlayer> entity) {
        super(entity);
    }

    @Override
    protected void doPonyRender(AbstractClientPlayer entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        if ("deadmau5".equals(entity.getName())) {
            getRenderer().bindTexture(entity.getLocationSkin());

            GlStateManager.pushMatrix();
            getPlayerModel().transform(BodyPart.HEAD);
            getPlayerModel().bipedHead.postRender(scale);

            GlStateManager.scale(1.3333334F, 1.3333334F, 1.3333334F);
            GlStateManager.translate(0, 0.3F, 0);

            deadMau5.setVisible(true);
            deadMau5.render(entity, move, swing, partialTicks, 0, 0, scale);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
