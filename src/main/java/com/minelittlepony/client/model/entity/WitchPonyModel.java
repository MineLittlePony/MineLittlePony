package com.minelittlepony.client.model.entity;

import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.entity.race.ZebraModel;
import com.minelittlepony.client.render.EquineRenderManager;

public class WitchPonyModel extends ZebraModel<WitchEntity> {

    public WitchPonyModel() {
        super(false);
        attributes.visualHeight = 2.5F;
    }

    @Override
    public void updateLivingState(WitchEntity entity, IPony pony, EquineRenderManager.Mode mode) {
        super.updateLivingState(entity, pony, mode);

        if (entity.hasCustomName() && "Filly".equals(entity.getCustomName().getString())) {
            child = true;
        }
        leftArmPose = ArmPose.EMPTY;
        rightArmPose = entity.getMainHandStack().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
    }

    @Override
    public void setAngles(WitchEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

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

            float legDrinkingAngle = -1 * PI/3 + rot;

            rightArm.pitch = legDrinkingAngle;
            rightSleeve.pitch = legDrinkingAngle;
            rightArm.yaw = 0.1F;
            rightSleeve.yaw = 0.1F;
            rightArm.pivotX = 0.1F;
            rightSleeve.pivotX = 0.1F;

            if (rot > 0) {
                rot = 0;
            }

            head.pitch = -rot / 2;
            helmet.pitch = -rot / 2;
        } else {
            rightArm.pivotX = 0;
            rightSleeve.pivotX = 0;
        }
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        if (wearable == Wearable.HAT) {
            return true;
        }
        return super.isWearing(wearable);
    }
}
