package com.minelittlepony.model.armour;

import net.minecraft.entity.Entity;

/**
 * Armour for skeleton ponies.
 *
 */
public class ModelSkeletonPonyArmor extends ModelPonyArmor {

    @Override
    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        super.rotateLegs(move, swing, tick, entity);
        aimBow(leftArmPose, rightArmPose, tick);
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY && !metadata.hasMagic()) {
            bipedRightArm.setRotationPoint(-1.5F, 9.5F, 4.0F);
        }
    }
}
