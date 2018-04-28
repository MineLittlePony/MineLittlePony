package com.minelittlepony.model.armour;

/**
 * Armour for skeleton ponies.
 *
 */
public class ModelSkeletonPonyArmor extends ModelPonyArmor {

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY && !canCast()) {
            bipedRightArm.setRotationPoint(-1.5F, 9.5F, 4);
        }
    }
}
