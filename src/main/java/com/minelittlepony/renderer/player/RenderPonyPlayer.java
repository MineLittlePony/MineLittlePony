package com.minelittlepony.renderer.player;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IPonyAnimationHolder;
import com.minelittlepony.model.PlayerModel;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderPonyPlayer extends RenderPonyBase {
    public RenderPonyPlayer(RenderManager renderManager, boolean useSmallArms, String id, PlayerModel model) {
        super(renderManager, useSmallArms, id, model);
    }
  
    @Override
    protected float getPonyShadowScale() {
        if (!MineLittlePony.getConfig().showscale) return .5f;
        return getPony().getMetadata().getSize().getShadowSize();
    }
  
    @Override
    protected float getScaleFactor() {
        return getPony().getMetadata().getSize().getScaleFactor();
    }

    @Override
    protected void transformElytraFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float ticks) {
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, -1, 0);
    }

    @Override
    protected void transformPegasusFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float yaw, float pitch, float ticks) {
        double dist = Math.sqrt(motionX * motionX + motionZ * motionZ);
        double angle = Math.atan2(motionY, dist);
        
        if (!player.capabilities.isFlying) {
            if (angle > 0) {
                angle = 0;
            } else {
                angle /= 2;
            }
        }

        if (angle > Math.PI / 3) angle = Math.PI / 3;
        if (angle < -Math.PI / 3) angle = -Math.PI / 3;
        
        ponyModel.motionPitch = (float) Math.toDegrees(angle);
        
        GlStateManager.rotate(ponyModel.motionPitch, 1, 0, 0);
        GlStateManager.rotate(((IPonyAnimationHolder)player).getStrafeAmount(ticks), 0, 0, 1);
    }
}
