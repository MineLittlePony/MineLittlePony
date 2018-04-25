package com.minelittlepony.model.armour;

public class ModelSkeletonPonyArmor extends ModelPonyArmor {

    @Override
    protected void rotateRightArm(float var8, float var9, float move, float tick) {
        if (this.rightArmPose == ArmPose.EMPTY) return;
        
        if (!this.metadata.hasMagic()) {
            rotateArmHolding(bipedRightArm, 1, var8, var9, tick);
        } else {
            rotateArmHolding(unicornArmRight, 1, var8, var9, tick);
        }
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (this.rightArmPose != ArmPose.EMPTY && !this.metadata.hasMagic()) {
            this.bipedRightArm.setRotationPoint(-1.5F, 9.5F, 4.0F);
        }

    }
}
