package com.minelittlepony.model.ponies;

import com.minelittlepony.model.ModelMobPony;
import com.minelittlepony.model.armour.ModelSkeletonPonyArmor;
import com.minelittlepony.model.armour.PonyArmor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class ModelSkeletonPony extends ModelMobPony {

    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new ModelSkeletonPonyArmor(), new ModelSkeletonPonyArmor());
    }

    @Override
    public boolean isCasting() {
        return true;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float move, float swing, float ticks) {
        rightArmPose = ArmPose.EMPTY;
        leftArmPose = ArmPose.EMPTY;
        ItemStack itemstack = entity.getHeldItem(EnumHand.MAIN_HAND);

        if (itemstack.getItem() == Items.BOW && ((AbstractSkeleton)entity).isSwingingArms())
        {
            if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                rightArmPose = ArmPose.BOW_AND_ARROW;
            } else {
                leftArmPose = ArmPose.BOW_AND_ARROW;
            }
        }

        super.setLivingAnimations(entity, move, swing, ticks);
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (rightArmPose != ArmPose.EMPTY && !canCast()) {
            bipedRightArm.setRotationPoint(-1.5F, 9.5F, 4);
        }
    }

    protected int getArmWidth() {
        return 2;
    }

    protected int getArmDepth() {
        return 2;
    }

    protected float getLegRotationX() {
        return 3;
    }

    protected float getArmRotationY() {
        return 8;
    }
}
