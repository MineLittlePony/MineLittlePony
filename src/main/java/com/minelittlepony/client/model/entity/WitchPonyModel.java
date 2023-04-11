package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;

public class WitchPonyModel extends EarthPonyModel<WitchEntity> {

    public WitchPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void updateLivingState(WitchEntity entity, IPony pony, ModelAttributes.Mode mode) {
        super.updateLivingState(entity, pony, mode);

        if (entity.hasCustomName() && "Filly".equals(entity.getCustomName().getString())) {
            child = true;
        }
        attributes.visualHeight += 0.5F;
        leftArmPose = ArmPose.EMPTY;
        rightArmPose = entity.getMainHandStack().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
    }

    @Override
    public void setModelAngles(WitchEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setModelAngles(entity, move, swing, ticks, headYaw, headPitch);

        if (entity.isDrinking()) {
            float noseRot = MathHelper.sin(entity.age);

            snout.rotate(noseRot * 4.5F * 0.02F, 0, noseRot * 2.5F * 0.02F);
        } else {
            snout.rotate(0, 0, 0);
        }

        if (rightArmPose != ArmPose.EMPTY) {
            float rot = (float)(Math.tan(ticks / 7) + Math.sin(ticks / 3));
            if (rot > 1) rot = 1;
            if (rot < -1) rot = -1;

            float legDrinkingAngle = -1 * MathHelper.PI/3 + rot;

            rightArm.pitch = legDrinkingAngle;
            rightArm.yaw = 0.1F;
            rightArm.pivotX = 0.1F;

            if (rot > 0) {
                rot = 0;
            }

            head.pitch = -rot / 2;
        } else {
            rightArm.pivotX = 0;
        }
    }

    @Override
    protected void positionheldItem(Arm arm, MatrixStack matrices) {
        super.positionheldItem(arm, matrices);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(10));
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        if (wearable == Wearable.HAT) {
            return true;
        }
        return super.isWearing(wearable);
    }
}
