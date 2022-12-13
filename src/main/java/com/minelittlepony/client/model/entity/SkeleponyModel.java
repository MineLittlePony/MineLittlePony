package com.minelittlepony.client.model.entity;

import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

import com.minelittlepony.client.model.IMobModel;
import com.minelittlepony.client.model.entity.race.AlicornModel;

public class SkeleponyModel<T extends HostileEntity> extends AlicornModel<T> implements IMobModel {

    public boolean isUnicorn;

    public boolean isWithered;

    public SkeleponyModel(ModelPart tree) {
        super(tree, false);
        this.vestRenderList.clear();
        this.sleevesRenderList.clear();
    }

    @Override
    public void animateModel(T entity, float move, float swing, float ticks) {
        isUnicorn = entity.getUuid().getLeastSignificantBits() % 3 != 0;
        isWithered = entity instanceof WitherSkeletonEntity;

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
            rotateArmHolding(getArm(Arm.RIGHT), -1, getSwingAmount(), ticks);
        }

        if (leftArmPose != ArmPose.EMPTY) {
            rotateArmHolding(getArm(Arm.LEFT), -1, getSwingAmount(), ticks);
        }
    }

    protected void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        IMobModel.rotateArmHolding(arm, direction, swingProgress, ticks);
    }

    @Override
    public boolean hasMagic() {
        return isUnicorn;
    }

    @Override
    protected float getLegOutset() {
        if (attributes.isSleeping) return 2.6f;
        if (attributes.isCrouching) return 0;
        return 4;
    }
}
