package com.minelittlepony.model.armour;

/**
 * Armour for skeleton ponies.
 *
 */
public class ModelSkeletonPonyArmor extends ModelPonyArmor {

    /**
     * The code here is copied from ModelMobPony, all with but one line of difference.
     */
    @Override
    protected void rotateRightArm(float move, float tick) {
        if (this.rightArmPose == ArmPose.EMPTY) return;
        
        if (!this.metadata.hasMagic()) {
            rotateArmHolding(bipedRightArm, 1, swingProgress, tick);
        } else {
            // With everything that's happening in ModelPonyArmor,
            // it's hard to tell if this is need or not.
            // Testing will probably reveal all.
            //unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmRight, 1, swingProgress, tick);
        }
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (this.rightArmPose != ArmPose.EMPTY && !this.metadata.hasMagic()) {
            this.bipedRightArm.setRotationPoint(-1.5F, 9.5F, 4.0F);
        }

    }
}
