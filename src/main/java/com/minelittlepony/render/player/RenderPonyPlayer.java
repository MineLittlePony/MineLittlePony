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

    private float sensibleAngle(float angle) {
        angle %= 360;  // if you need to copy-paste a portion of code more than 2 times - make a function
        if (angle > 180) angle -= 360;
        if (angle < -180) angle += 360;
        return angle;
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
            yaw = sensibleAngle(player.cameraYaw - player.rotationYawHead);  // will need later
            // since model roll should probably be calculated from model rotation rather than entity rotation...
            double roll = sensibleAngle(player.prevRenderYawOffset - player.renderYawOffset);
            float modelYaw = sensibleAngle(player.renderYawOffset);
            // filtering ugly jitter that occurs in Vanilla code if motion changes from sideways to diagonal
            if (Math.abs(roll) > 0.5f && Math.abs(sensibleAngle(modelYaw + yaw)) > 40) {
                return;
            }
            // detecting that we're flying backwards and roll must be inverted
            if (Math.abs(sensibleAngle((float) Math.toDegrees(Math.atan2(motionX, motionZ)) + modelYaw)) > 90) {
                roll *= -1;  // because inline ifs are not in favor
            }
            // ayyy magic numbers (after 5 - an approximation of nice looking coefficients calculated by hand)
            roll *= horMotion * 5 * (3.6884f * Math.pow(Math.abs(roll), -0.191));
            roll = MathHelper.clamp(roll, -54, 54);  // safety measure, shouldn't be required anymore

            GlStateManager.rotate((float)roll, 0, 0, 1);
        }

    }

    //TODO: MC1.13 transformSwimming()
}
