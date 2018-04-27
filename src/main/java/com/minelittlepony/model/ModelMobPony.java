package com.minelittlepony.model;

import com.minelittlepony.model.ponies.ModelPlayerPony;

import net.minecraft.entity.Entity;

/**
 * Common class for all humanoid (ponioid?) non-player enemies.
 *
 */
public class ModelMobPony extends ModelPlayerPony {

    public ModelMobPony() {
        super(false);
    }

    @Override
    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        super.rotateLegs(move, swing, tick, entity);

        rotateRightArm(move, tick);
        rotateLeftArm(move, tick);
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
