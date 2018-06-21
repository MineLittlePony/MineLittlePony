package com.minelittlepony.transform;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class PostureFlight implements PonyPosture<AbstractClientPlayer> {
    @Override
    public boolean applies(EntityLivingBase entity) {
        return entity instanceof AbstractClientPlayer;
    }

    private double calculateRoll(AbstractClientPlayer player, double motionX, double motionY, double motionZ) {

        // since model roll should probably be calculated from model rotation rather than entity rotation...
        double roll = MathUtil.sensibleAngle(player.prevRenderYawOffset - player.renderYawOffset);
        double horMotion = Math.sqrt(motionX * motionX + motionZ * motionZ);
        float modelYaw = MathUtil.sensibleAngle(player.renderYawOffset);

        // detecting that we're flying backwards and roll must be inverted
        if (Math.abs(MathUtil.sensibleAngle((float) Math.toDegrees(Math.atan2(motionX, motionZ)) + modelYaw)) > 90) {
            roll *= -1;
        }

        // ayyy magic numbers (after 5 - an approximation of nice looking coefficients calculated by hand)

        // roll might be zero, in which case Math.pow produces +Infinity. Anything x Infinity = NaN.
        double pow = roll != 0 ? Math.pow(Math.abs(roll), -0.191) : 0;

        roll *= horMotion * 5 * (3.6884f * pow);

        assert !Float.isNaN((float)roll);

        return MathHelper.clamp(roll, -54, 54);
    }


    @Override
    public void transform(AbstractPonyModel model, AbstractClientPlayer player, double motionX, double motionY, double motionZ, float pitch, float yaw, float ticks) {
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

        model.motionPitch = (float) Math.toDegrees(angle);

        GlStateManager.rotate(model.motionPitch, 1, 0, 0);

        float roll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        roll = model.getMetadata().getInterpolator().interpolate("pegasusRoll", roll, 10);

        GlStateManager.rotate((float)roll, 0, 0, 1);
    }
}
