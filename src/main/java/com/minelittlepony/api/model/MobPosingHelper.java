package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Arm;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.mson.util.PartUtil;

public final class MobPosingHelper {
    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     *
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param ticks         Render partial ticks
     */
    @Deprecated
    public static void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        float swing = MathHelper.sin(swingProgress * MathHelper.PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * MathHelper.PI);

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) / 10;

        arm.pitch = -1.5707964F;
        arm.pitch -= swing * 1.2F - roll * 0.4F;
        arm.pitch += sin;

        arm.yaw = direction * (0.1F - swing * 0.6F);
        arm.roll = cos;
    }

    public static <T extends BipedEntityRenderState> void animateZombieArms(ModelPart leftArm, ModelPart rightArm, boolean aggressive, T state) {
        if (!state.baby || state.getMainHandItemStack().isEmpty()) {
            boolean animateAttack = state.swingAnimationType != SwingAnimationType.STAB;
            if (animateAttack) {
                float attackTime = state.handSwingProgress;
                float armDrop = -MathHelper.PI / (aggressive ? 1.9F : 2.25F);
                float attackYRotModifier = MathHelper.sin(attackTime * (float) MathHelper.PI);
                float attackXRotModifier = MathHelper.sin((1 - (1 - attackTime) * (1 - attackTime)) * (float) MathHelper.PI);
                if (state.preferredArm == Arm.RIGHT) {
                    rightArm.roll = 0;
                    rightArm.yaw = -(0.1F - attackYRotModifier * 0.6F);
                    rightArm.pitch = armDrop;
                    rightArm.pitch += attackYRotModifier * 1.2F - attackXRotModifier * 0.4F;
                    rightArm.originZ++;
                } else {
                    leftArm.roll = 0.0F;
                    leftArm.yaw = 0.1F - attackYRotModifier * 0.6F;
                    leftArm.pitch = armDrop;
                    leftArm.pitch += attackYRotModifier * 1.2F - attackXRotModifier * 0.4F;
                    leftArm.originZ++;
                }
            }

            ArmPosing.swingArms(rightArm, leftArm, state.age);
        }
    }

    @Deprecated
    public static void rotateUndeadArms(PonyModel.AttributedHolder attributes, PonyModel<?> model, float limbAngle, float ticks) {
        if (islookAngleRight(limbAngle)) {
            ModelPart rightArm = model.getForeLeg(Arm.RIGHT);
            rotateArmHolding(rightArm, 1, attributes.getSwingAmount(), ticks);
            if (attributes.getAttributes().isSitting) {
                rightArm.pitch += 0.6F;
            }
            PartUtil.shift(rightArm, 0.5F, 1.5F, 3);
        } else {
            ModelPart leftArm = model.getForeLeg(Arm.LEFT);
            rotateArmHolding(leftArm, -1, attributes.getSwingAmount(), ticks);
            if (attributes.getAttributes().isSitting) {
                leftArm.pitch += 0.6F;
            }
            PartUtil.shift(leftArm, -0.5F, 1.5F, 3);
        }
    }

    @Deprecated
    public static boolean islookAngleRight(float limbAngle) {
        return MathHelper.sin(limbAngle / 20) < 0;
    }
}
