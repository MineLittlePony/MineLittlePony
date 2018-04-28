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

    @Override
    protected void adjustLegs(float move, float swing, float tick) {
        super.adjustLegs(move, swing, tick);
        rotateRightArm(move, tick);
        rotateLeftArm(move, tick);
    }

    /**
     * Returns true if the angle is to the right?
     */
    public boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20f) < 0;
    }

    /**
     * Called to update the left arm's final rotation.
     * Subclasses may replace it with their own implementations.
     *
     * @param move Limb swing amount.
     * @param tick Render partial ticks.
     */
    protected void rotateRightArm(float move, float tick) {
        if (rightArmPose == ArmPose.EMPTY) return;

        if (!metadata.hasMagic()) {
            rotateArmHolding(bipedRightArm, -1, swingProgress, tick);
        } else {
            unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmRight, -1, swingProgress, tick);
        }
    }

    /**
     * Same as rotateRightArm but for the left arm (duh).
     *
     * @param move Limb swing amount.
     * @param tick Render partial ticks.
     */
    protected void rotateLeftArm(float move, float tick) {
        if (leftArmPose == ArmPose.EMPTY) return;

        if (!metadata.hasMagic()) {
            rotateArmHolding(bipedLeftArm, -1, swingProgress, tick);
        } else {
            unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmLeft, -1, swingProgress, tick);
        }
    }
}
