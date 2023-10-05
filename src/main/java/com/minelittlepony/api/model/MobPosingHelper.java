package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.mson.util.PartUtil;

public final class MobPosingHelper {
    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     *
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param ticks         Render partial ticks
     */
    public static void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        float swing = MathHelper.sin(swingProgress * MathHelper.PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * MathHelper.PI);

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) / 10;

        arm.pitch = -1.5707964F;
        arm.pitch -= swing * 1.2F - roll * 0.4F;
        arm.pitch += sin;

        arm.yaw = direction * (0.1F - swing * 0.6F);
        arm.roll = cos;
    }

    public static void rotateUndeadArms(PonyModel<?> model, float move, float ticks) {
        ModelPart leftArm = model.getForeLeg(Arm.LEFT);
        ModelPart rightArm = model.getForeLeg(Arm.RIGHT);

        if (islookAngleRight(move)) {
            rotateArmHolding(rightArm, 1, model.getSwingAmount(), ticks);
            if (model.getAttributes().isSitting) {
                rightArm.pitch += 0.6F;
            }
            PartUtil.shift(rightArm, 0.5F, 1.5F, 3);
        } else {
            rotateArmHolding(leftArm, -1, model.getSwingAmount(), ticks);
            if (model.getAttributes().isSitting) {
                leftArm.pitch += 0.6F;
            }
            PartUtil.shift(leftArm, -0.5F, 1.5F, 3);
        }
    }

    public static boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20) < 0;
    }
}
