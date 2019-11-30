package com.minelittlepony.client.model.entity;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.mson.api.model.MsonPart;

public class ZomponyVillagerModel extends VillagerPonyModel<ZombieVillagerEntity> implements IMobModel {

    @Override
    protected void rotateLegs(float move, float swing, float ticks, ZombieVillagerEntity entity) {
        super.rotateLegs(move, swing, ticks, entity);

        if (rightArmPose == ArmPose.EMPTY) {
            if (islookAngleRight(move)) {
                rotateArmHolding(rightArm, 1, getSwingAmount(), ticks);
                ((MsonPart)rightArm).shift(0.5F, 1.5F, 3);
            } else {
                rotateArmHolding(leftArm, -1, getSwingAmount(), ticks);
                ((MsonPart)leftArm).shift(-0.5F, 1.5F, 3);
            }
        }
    }

    public boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20) < 0;
    }
}
