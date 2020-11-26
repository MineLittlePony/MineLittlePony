package com.minelittlepony.client.model.entity;

import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.mson.api.model.MsonPart;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;

public class ZomponyModel<Zombie extends HostileEntity> extends AlicornModel<Zombie> implements IMobModel {

    private boolean isPegasus;

    public ZomponyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void animateModel(Zombie entity, float move, float swing, float ticks) {
        super.animateModel(entity, move, swing, ticks);
        isPegasus = entity.getUuid().getLeastSignificantBits() % 30 == 0;
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Zombie entity) {
        super.rotateLegs(move, swing, ticks, entity);

        if (isZombified(entity)) {
            if (islookAngleRight(move)) {
                rotateArmHolding(rightArm, 1, getSwingAmount(), ticks);
                ((MsonPart)(Object)rightArm).shift(0.5F, 1.5F, 3);
            } else {
                rotateArmHolding(leftArm, -1, getSwingAmount(), ticks);
                ((MsonPart)(Object)leftArm).shift(-0.5F, 1.5F, 3);
            }
        }
    }

    protected boolean isZombified(Zombie entity) {
        return rightArmPose == ArmPose.EMPTY;
    }

    public boolean islookAngleRight(float move) {
        return MathHelper.sin(move / 20) < 0;
    }

    @Override
    public boolean canFly() {
        return isPegasus;
    }
}
