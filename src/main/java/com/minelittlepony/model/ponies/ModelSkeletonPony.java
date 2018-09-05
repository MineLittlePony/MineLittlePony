package com.minelittlepony.model.ponies;

import com.minelittlepony.model.ModelMobPony;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class ModelSkeletonPony extends ModelMobPony {

    public boolean isUnicorn;

    public boolean isWithered;

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float move, float swing, float ticks) {
        isUnicorn = entity.getUniqueID().getLeastSignificantBits() % 3 != 0;
        isWithered = entity instanceof EntityWitherSkeleton;

        rightArmPose = ArmPose.EMPTY;
        leftArmPose = ArmPose.EMPTY;

        ItemStack mainHand = entity.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offHand = entity.getHeldItem(EnumHand.OFF_HAND);

        boolean right = entity.getPrimaryHand() == EnumHandSide.RIGHT;

        if (!offHand.isEmpty()) {
            if (right) {
                leftArmPose = ArmPose.ITEM;
            } else {
                rightArmPose = ArmPose.ITEM;
            }
        }

        if (!mainHand.isEmpty()) {
            ArmPose pose = mainHand.getItem() == Items.BOW && ((AbstractSkeleton)entity).isSwingingArms() ? ArmPose.BOW_AND_ARROW : ArmPose.ITEM;

            if (right) {
                rightArmPose = pose;
            } else {
                leftArmPose = pose;
            }
        }


        super.setLivingAnimations(entity, move, swing, ticks);
    }

    @Override
    public boolean canCast() {
        return isUnicorn;
    }

    @Override
    protected float getLegOutset() {
        if (isSleeping()) return 2.6f;
        if (isCrouching()) return 0;
        return 4;
    }

    @Override
    protected int getArmWidth() {
        return 2;
    }

    @Override
    protected int getArmDepth() {
        return 2;
    }

    @Override
    protected float getLegRotationX() {
        return 3;
    }

    @Override
    public float getModelHeight() {
        return isWithered ? 2.5F : 2;
    }

    @Override
    protected float getArmRotationY() {
        return 8;
    }
}
