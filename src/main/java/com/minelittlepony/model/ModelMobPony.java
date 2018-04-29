package com.minelittlepony.model;

import com.minelittlepony.model.player.ModelAlicorn;

import net.minecraft.util.math.MathHelper;

/**
 * Common class for all humanoid (ponioid?) non-player enemies.
 *
 */
public class ModelMobPony extends ModelAlicorn {

    public ModelMobPony() {
        super(false);
    }

    /**
     * Returns true if the angle is to the right?
     */
    public boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20f) < 0;
    }

    @Override
    protected void adjustLegs(float move, float swing, float tick) {
        super.adjustLegs(move, swing, tick);
        if (rightArmPose != ArmPose.EMPTY) {
            if (canCast()) {
                unicornArmRight.setRotationPoint(-7, 12, -2);
                rotateArmHolding(unicornArmRight, -1, swingProgress, tick);
            } else {
                rotateArmHolding(bipedRightArm, -1, swingProgress, tick);
            }
        }

        if (leftArmPose != ArmPose.EMPTY) {
            if (!canCast()) {
                unicornArmRight.setRotationPoint(-7, 12, -2);
                rotateArmHolding(unicornArmLeft, -1, swingProgress, tick);
            } else {
                rotateArmHolding(bipedLeftArm, -1, swingProgress, tick);
            }
        }
    }
}
