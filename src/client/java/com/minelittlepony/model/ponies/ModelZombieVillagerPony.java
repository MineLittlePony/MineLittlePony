package com.minelittlepony.model.ponies;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.util.render.AbstractBoxRenderer;

public class ModelZombieVillagerPony extends ModelVillagerPony {

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);
        if (rightArmPose != ArmPose.EMPTY) return;

        if (islookAngleRight(move)) {
            rotateArmHolding(bipedRightArm, 1, swingProgress, ticks);
            AbstractBoxRenderer.shiftRotationPoint(bipedRightArm, 0.5F, 1.5F, 3);
        } else {
            rotateArmHolding(bipedLeftArm, -1, swingProgress, ticks);
            AbstractBoxRenderer.shiftRotationPoint(bipedLeftArm, -0.5F, 1.5F, 3);
        }
    }

    public boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20) < 0;
    }
}
