package com.minelittlepony.render.player;

import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;

public class RenderPonyPlayer extends RenderPonyBase {

    public RenderPonyPlayer(RenderManager renderManager, boolean useSmallArms, ModelWrapper model) {
        super(renderManager, useSmallArms, model);
    }

    @Override
    public float getShadowScale() {
        return getPony().getMetadata().getSize().getShadowSize();
    }

    @Override
    public float getScaleFactor() {
        return getPony().getMetadata().getSize().getScaleFactor();
    }

    @Override
    protected void transformElytraFlight(AbstractClientPlayer player, double motionX, double motionY, double motionZ, float ticks) {
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, player.isSneaking() ? 0.2F : -1, 0);
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

        angle = MathUtil.clampLimit(angle, Math.PI / 3);

        ponyModel.motionPitch = (float) Math.toDegrees(angle);

        GlStateManager.rotate(ponyModel.motionPitch, 1, 0, 0);

        double horMotion = Math.sqrt(motionX * motionX + motionZ * motionZ);

        if (horMotion > 0) {

            yaw = player.cameraYaw - player.rotationYawHead;

            double roll = (Math.toDegrees(Math.atan2(motionX, motionZ)) - yaw) % 360;

            if (roll < -180) roll += 360;
            if (roll > 180) roll -= 360;

            roll *= horMotion * 2;
            roll = MathHelper.clamp(roll, -54, 54);

            GlStateManager.rotate((float)roll, 0, 0, 1);
        }

    }

    //TODO: MC1.13 transformSwimming()
}
