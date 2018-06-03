package com.minelittlepony.model.ponies;

import com.minelittlepony.model.ModelMobPony;
import com.minelittlepony.render.AbstractPonyRenderer;

import net.minecraft.entity.Entity;

public class ModelZombiePony extends ModelMobPony {
    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);
        if (rightArmPose != ArmPose.EMPTY) return;

        if (islookAngleRight(move)) {
            rotateArmHolding(bipedRightArm, 1, swingProgress, ticks);
        } else {
            rotateArmHolding(bipedLeftArm, -1, swingProgress, ticks);
        }
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY) return;

        if (islookAngleRight(move)) {
            AbstractPonyRenderer.shiftRotationPoint(bipedRightArm, 0.5F, 1.5F, 3);
        } else {
            AbstractPonyRenderer.shiftRotationPoint(bipedLeftArm, -0.5F, 1.5F, 3);
        }
    }
}
