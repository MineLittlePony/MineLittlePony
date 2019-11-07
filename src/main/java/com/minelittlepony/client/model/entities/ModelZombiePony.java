package com.minelittlepony.client.model.entities;

import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.client.model.races.ModelAlicorn;
import com.minelittlepony.client.util.render.Part;

import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;

public class ModelZombiePony<Zombie extends HostileEntity> extends ModelAlicorn<Zombie> implements IMobModel {

    public boolean isPegasus;

    public ModelZombiePony() {
        super(false);
    }

    @Override
    public void animateModel(Zombie entity, float move, float swing, float ticks) {
        isPegasus = entity.getUuid().getLeastSignificantBits() % 30 == 0;
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Zombie entity) {
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

    @Override
    public boolean canFly() {
        return isPegasus;
    }
}
