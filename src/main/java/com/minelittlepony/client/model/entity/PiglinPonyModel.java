package com.minelittlepony.client.model.entity;

import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.render.EquineRenderManager;

public class PiglinPonyModel extends ZomponyModel<HostileEntity> {

    private PiglinEntity.Activity activity;

    @Override
    public void updateLivingState(HostileEntity entity, IPony pony, EquineRenderManager.Mode mode) {
        super.updateLivingState(entity, pony, mode);
        leftArmPose = ArmPose.EMPTY;
        rightArmPose = entity.getMainHandStack().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;

        if (entity instanceof PiglinEntity) {
            PiglinEntity piglinEntity = (PiglinEntity)entity;
            activity = piglinEntity.getActivity();

            if (activity == PiglinEntity.Activity.CROSSBOW_HOLD) {
                rightArmPose = ArmPose.CROSSBOW_HOLD;
            } else if (activity == PiglinEntity.Activity.CROSSBOW_CHARGE) {
                rightArmPose = ArmPose.CROSSBOW_CHARGE;
            } else if (activity == PiglinEntity.Activity.ADMIRING_ITEM) {
                leftArmPose = ArmPose.ITEM;
            }
        } else {
            activity = PiglinEntity.Activity.DEFAULT;
        }
    }

    @Override
    public void setAngles(HostileEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

        if (activity == PiglinEntity.Activity.ADMIRING_ITEM) {
            leftArm.yaw = 0.5F;
            leftArm.pitch = -1.9F;
            leftArm.pivotY += 4;
            leftArm.pivotZ += 3;
            leftArm.pivotX += 2;
            head.pitch = MathHelper.sin(ticks / 12) / 6 + 0.5F;
            head.yaw = 0;

            head.roll = MathHelper.sin(ticks / 10) / 3F;


            helmet.copyPositionAndRotation(head);
        }
    }

    @Override
    protected boolean isZombified(HostileEntity entity) {
        return !(entity instanceof PiglinEntity);
    }
}
