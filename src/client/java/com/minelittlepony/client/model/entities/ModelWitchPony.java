package com.minelittlepony.client.model.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.races.ModelZebra;
import com.minelittlepony.common.pony.IPony;
import com.minelittlepony.common.pony.meta.Wearable;

public class ModelWitchPony extends ModelZebra {

    public ModelWitchPony() {
        super(false);
    }

    @Override
    public void updateLivingState(EntityLivingBase entity, IPony pony) {
        super.updateLivingState(entity, pony);
        EntityWitch witch = ((EntityWitch) entity);

        if ("Filly".equals(entity.getCustomNameTag())) {
            isChild = true;
        }
        leftArmPose = ArmPose.EMPTY;
        rightArmPose = witch.getHeldItemMainhand().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {
        super.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);

        if (((EntityWitch)entity).isDrinkingPotion()) {
            float noseRot = MathHelper.sin(entity.ticksExisted);

            snout.rotate(noseRot * 4.5F * 0.02F, 0, noseRot * 2.5F * 0.02F);
        } else {
            snout.rotate(0, 0, 0);
        }


        if (rightArmPose != ArmPose.EMPTY) {
            float rot = (float)(Math.tan(ticks / 7) + Math.sin(ticks / 3));
            if (rot > 1) rot = 1;
            if (rot < -1) rot = -1;

            float legDrinkingAngle = -1 * PI/3 + rot;

            bipedRightArm.rotateAngleX = legDrinkingAngle;
            bipedRightArmwear.rotateAngleX = legDrinkingAngle;
            bipedRightArm.rotateAngleY = 0.1F;
            bipedRightArmwear.rotateAngleY = 0.1F;
            bipedRightArm.offsetZ = 0.1f;
            bipedRightArmwear.offsetZ = 0.1f;

            if (rot > 0) rot = 0;

            bipedHead.rotateAngleX = -rot / 2;
            bipedHeadwear.rotateAngleX = -rot / 2;
        } else {
            bipedRightArm.offsetZ = 0;
            bipedRightArmwear.offsetZ = 0;
        }


    }

    @Override
    public boolean isChild() {
        return isChild;
    }

    @Override
    public float getModelHeight() {
        return 2.5F;
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        if (wearable == Wearable.HAT) {
            return true;
        }
        return super.isWearing(wearable);
    }
}
