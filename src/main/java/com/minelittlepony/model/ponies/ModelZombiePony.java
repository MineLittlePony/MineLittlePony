package com.minelittlepony.model.ponies;

import com.minelittlepony.model.ModelMobPony;
import com.minelittlepony.render.AbstractPonyRenderer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelZombiePony extends ModelMobPony {

    public boolean isPegasus;

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float move, float swing, float ticks) {
        isPegasus = entity.getUniqueID().getLeastSignificantBits() % 30 == 0;
        isFlying = !entity.onGround;
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, Entity entity) {
        super.rotateLegs(move, swing, ticks, entity);
        if (rightArmPose != ArmPose.EMPTY) return;

        if (islookAngleRight(move)) {
            rotateArmHolding(bipedRightArm, 1, swingProgress, ticks);
            AbstractPonyRenderer.shiftRotationPoint(bipedRightArm, 0.5F, 1.5F, 3);
        } else {
            rotateArmHolding(bipedLeftArm, -1, swingProgress, ticks);
            AbstractPonyRenderer.shiftRotationPoint(bipedLeftArm, -0.5F, 1.5F, 3);
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
