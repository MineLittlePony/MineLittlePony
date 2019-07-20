package com.minelittlepony.client.model.entities;

import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

import com.minelittlepony.client.model.ModelMobPony;

public class ModelSkeletonPony<T extends HostileEntity> extends ModelMobPony<T> {

    public boolean isUnicorn;

    public boolean isWithered;

    public ModelSkeletonPony() {
        super();
        attributes.armWidth = 2;
        attributes.armDepth = 2;
        attributes.armRotationX = 3F;
        attributes.armRotationY = 8F;
    }

    @Override
    public void animateModel(T entity, float move, float swing, float ticks) {
        isUnicorn = entity.getUuid().getLeastSignificantBits() % 3 != 0;
        isWithered = entity instanceof WitherSkeletonEntity;
        attributes.visualHeight = isWithered ? 2.5F : 2;

        rightArmPose = ArmPose.EMPTY;
        leftArmPose = ArmPose.EMPTY;

        ItemStack mainHand = entity.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHand = entity.getStackInHand(Hand.OFF_HAND);

        boolean right = entity.getMainArm() == Arm.RIGHT;

        if (!offHand.isEmpty()) {
            if (right) {
                leftArmPose = ArmPose.ITEM;
            } else {
                rightArmPose = ArmPose.ITEM;
            }
        }

        if (!mainHand.isEmpty()) {
            ArmPose pose = mainHand.getItem() == Items.BOW && entity.isAttacking() ? ArmPose.BOW_AND_ARROW : ArmPose.ITEM;

            if (right) {
                rightArmPose = pose;
            } else {
                leftArmPose = pose;
            }
        }
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, T entity) {
        super.rotateLegs(move, swing, ticks, entity);
        if (rightArmPose != ArmPose.EMPTY) {
            if (canCast()) {
                rotateArmHolding(unicornArmRight, -1, getSwingAmount(), ticks);
            } else {
                rotateArmHolding(rightArm, -1, getSwingAmount(), ticks);
            }
        }

        if (leftArmPose != ArmPose.EMPTY) {
            if (canCast()) {
                rotateArmHolding(unicornArmLeft, -1, getSwingAmount(), ticks);
            } else {
                rotateArmHolding(leftArm, -1, getSwingAmount(), ticks);
            }
        }
    }

    @Override
    public boolean canCast() {
        return isUnicorn;
    }

    @Override
    protected float getLegOutset() {
        if (attributes.isSleeping) return 2.6f;
        if (attributes.isCrouching) return 0;
        return 4;
    }
}
