package com.minelittlepony.client.model.entities;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.client.util.render.Part;

public class ModelZombieVillagerPony extends ModelVillagerPony<ZombieVillagerEntity> implements IMobModel {

    @Override
    protected void rotateLegs(float move, float swing, float ticks, ZombieVillagerEntity entity) {
        super.rotateLegs(move, swing, ticks, entity);
        if (rightArmPose != ArmPose.EMPTY) return;

        if (islookAngleRight(move)) {
            rotateArmHolding(rightArm, 1, getSwingAmount(), ticks);
            Part.shiftRotationPoint(rightArm, 0.5F, 1.5F, 3);
        } else {
            rotateArmHolding(leftArm, -1, getSwingAmount(), ticks);
            Part.shiftRotationPoint(leftArm, -0.5F, 1.5F, 3);
        }
    }

    public boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20) < 0;
    }
}
