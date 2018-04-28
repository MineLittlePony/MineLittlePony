package com.minelittlepony.model.armour;

/**
 * Armour for skeleton ponies.
 *
 */
public class ModelSkeletonPonyArmor extends ModelPonyArmor {

    @Override
    protected void adjustLegs(float move, float swing, float tick) {
        aimBow(leftArmPose, rightArmPose, tick);
        super.adjustLegs(move, swing, tick);
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY && !metadata.hasMagic()) {
            bipedRightArm.setRotationPoint(-1.5F, 9.5F, 4);
        }
    }
}
