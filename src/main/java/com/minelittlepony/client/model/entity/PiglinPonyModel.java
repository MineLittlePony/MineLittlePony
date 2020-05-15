package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.mson.api.ModelContext;

public class PiglinPonyModel extends ZomponyModel<HostileEntity> {

    private PiglinEntity.Activity activity;

    private ModelPart leftFlap;
    private ModelPart rightFlap;

    @Override
    public void init(ModelContext context) {
        super.init(context);
        leftFlap = context.findByName("left_flap");
        rightFlap = context.findByName("right_flap");
    }

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

        float progress = ticks * 0.1F + move * 0.5F;
        float range = 0.08F + swing * 0.4F;
        rightFlap.roll = -0.5235988F - MathHelper.cos(progress * 1.2F) * range;
        leftFlap.roll =   0.5235988F + MathHelper.cos(progress) * range;
    }

    @Override
    protected boolean isZombified(HostileEntity entity) {
        return !(entity instanceof PiglinEntity);
    }
}
