package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.IPony;

public class PiglinPonyModel extends ZomponyModel<HostileEntity> {

    private PiglinActivity activity;

    private final ModelPart leftFlap;
    private final ModelPart rightFlap;

    public PiglinPonyModel(ModelPart tree) {
        super(tree);
        leftFlap = tree.getChild("left_flap");
        rightFlap = tree.getChild("right_flap");
    }

    @Override
    public void updateLivingState(HostileEntity entity, IPony pony, ModelAttributes.Mode mode) {
        super.updateLivingState(entity, pony, mode);
        leftArmPose = ArmPose.EMPTY;
        rightArmPose = entity.getMainHandStack().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;

        if (entity instanceof AbstractPiglinEntity) {
            activity = ((AbstractPiglinEntity)entity).getActivity();

            if (activity == PiglinActivity.CROSSBOW_HOLD) {
                rightArmPose = ArmPose.CROSSBOW_HOLD;
            } else if (activity == PiglinActivity.CROSSBOW_CHARGE) {
                rightArmPose = ArmPose.CROSSBOW_CHARGE;
            } else if (activity == PiglinActivity.ADMIRING_ITEM) {
                leftArmPose = ArmPose.ITEM;
            }
        } else {
            activity = PiglinActivity.DEFAULT;
        }
    }

    @Override
    public void setModelAngles(HostileEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setModelAngles(entity, move, swing, ticks, headYaw, headPitch);

        float progress = ticks * 0.1F + move * 0.5F;
        float range = 0.08F + swing * 0.4F;
        rightFlap.roll = -0.5235988F - MathHelper.cos(progress * 1.2F) * range;
        leftFlap.roll =   0.5235988F + MathHelper.cos(progress) * range;
    }

    @Override
    protected void rotateLegs(float move, float swing, float ticks, HostileEntity entity) {
        super.rotateLegs(move, swing, ticks, entity);

        if (activity == PiglinActivity.ADMIRING_ITEM) {
            leftArm.yaw = 0.5F;
            leftArm.pitch = -1.9F;
            leftArm.pivotY += 4;
            leftArm.pivotZ += 3;
            leftArm.pivotX += 2;
            head.pitch = MathHelper.sin(ticks / 12) / 6 + 0.5F;
            head.yaw = 0;

            head.roll = MathHelper.sin(ticks / 10) / 3F;
        } else if (activity == PiglinActivity.DANCING) {

            float speed = ticks / 60;

            head.pivotX = MathHelper.sin(speed * 10);
            head.pivotY = MathHelper.sin(speed * 40) + 0.4F;
            head.pitch += MathHelper.sin(speed * 40) / 4 + 0.4F;

            float bodyBob = MathHelper.sin(speed * 40) * 0.35F;
            float legBob = MathHelper.sin(speed * 40) * 0.25F;

            neck.pivotY = bodyBob;
            body.pivotY = bodyBob;

            leftLeg.pitch += legBob;
            rightLeg.pitch -= legBob;

            leftArm.roll -= legBob/4;
            rightArm.roll += legBob/4;

            rightArm.pitch += legBob - 0.4F;
            leftArm.pitch -= legBob + 0.4F;
        }
    }

    @Override
    protected boolean isZombified(HostileEntity entity) {
        return !(entity instanceof AbstractPiglinEntity);
    }
}
