package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.mson.util.PartUtil;

/**
 * Common interface for all undead enemies.
 */
public interface IMobModel {
    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     *
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param ticks         Render partial ticks
     */
    static void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
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

    static void rotateUndeadArms(ClientPonyModel<?> model, float move, float ticks) {
        ModelPart leftArm = model.getArm(Arm.LEFT);
        ModelPart rightArm = model.getArm(Arm.RIGHT);

        if (islookAngleRight(move)) {
            IMobModel.rotateArmHolding(rightArm, 1, model.getSwingAmount(), ticks);
            PartUtil.shift(rightArm, 0.5F, 1.5F, 3);
        } else {
            IMobModel.rotateArmHolding(leftArm, -1, model.getSwingAmount(), ticks);
            PartUtil.shift(leftArm, -0.5F, 1.5F, 3);
        }
    }

    static boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20) < 0;
    }
}
