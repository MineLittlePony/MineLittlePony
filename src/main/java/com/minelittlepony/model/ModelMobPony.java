package com.minelittlepony.model;

import com.minelittlepony.model.player.ModelAlicorn;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Common class for all humanoid (ponioid?) non-player enemies.
 *
 */
public class ModelMobPony extends ModelAlicorn {

    public ModelMobPony() {
        super(false);
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);

        if (rightArmPose != ArmPose.EMPTY) {
            if (canCast()) {
                rotateArmHolding(unicornArmRight, -1, swingProgress, ticks);
            } else {
                rotateArmHolding(bipedRightArm, -1, swingProgress, ticks);
            }
        }

        if (leftArmPose != ArmPose.EMPTY) {
            if (canCast()) {
                rotateArmHolding(unicornArmLeft, -1, swingProgress, ticks);
            } else {
                rotateArmHolding(bipedLeftArm, -1, swingProgress, ticks);
            }
        }
    }

    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     *
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param ticks         Render partial ticks
     */
    protected void rotateArmHolding(ModelRenderer arm, float direction, float swingProgress, float ticks) {
        float swing = MathHelper.sin(swingProgress * PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * PI);

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) / 10;

        arm.rotateAngleX = -1.5707964F;
        arm.rotateAngleX -= swing * 1.2F - roll * 0.4F;
        arm.rotateAngleX += sin;

        arm.rotateAngleY = direction * (0.1F - swing * 0.6F);
        arm.rotateAngleZ = cos;
    }
}
