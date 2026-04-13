package com.minelittlepony.api.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.SwingAnimationType;

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
        float swing = Mth.sin(swingProgress * Mth.PI);
        float roll = Mth.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * Mth.PI);

        float cos = Mth.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = Mth.sin(ticks * 0.067F) / 10;

        arm.xRot = -1.5707964F;
        arm.xRot -= swing * 1.2F - roll * 0.4F;
        arm.xRot += sin;

        arm.yRot = direction * (0.1F - swing * 0.6F);
        arm.zRot = cos;
    }

    public static <T extends HumanoidRenderState> void animateZombieArms(ModelPart leftArm, ModelPart rightArm, boolean aggressive, T state) {
        if (!state.isBaby || state.getMainHandItemStack().isEmpty()) {
            boolean animateAttack = state.swingAnimationType != SwingAnimationType.STAB;
            if (animateAttack) {
                float attackTime = state.attackTime;
                float armDrop = -Mth.PI / (aggressive ? 1.9F : 2.25F);
                float attackYRotModifier = Mth.sin(attackTime * (float) Mth.PI);
                float attackXRotModifier = Mth.sin((1 - (1 - attackTime) * (1 - attackTime)) * (float) Mth.PI);
                if (state.attackArm == HumanoidArm.RIGHT) {
                    rightArm.zRot = 0;
                    rightArm.yRot = -(0.1F - attackYRotModifier * 0.6F);
                    rightArm.xRot = armDrop;
                    rightArm.xRot += attackYRotModifier * 1.2F - attackXRotModifier * 0.4F;
                    rightArm.z++;
                } else {
                    leftArm.zRot = 0.0F;
                    leftArm.yRot = 0.1F - attackYRotModifier * 0.6F;
                    leftArm.xRot = armDrop;
                    leftArm.xRot += attackYRotModifier * 1.2F - attackXRotModifier * 0.4F;
                    leftArm.z++;
                }
            }

            AnimationUtils.bobArms(rightArm, leftArm, state.ageInTicks);
        }
    }

    @Deprecated
    public static void rotateUndeadArms(PonyModel.AttributedHolder attributes, PonyModel<?> model, float limbAngle, float ticks) {
        if (islookAngleRight(limbAngle)) {
            ModelPart rightArm = model.getForeLeg(HumanoidArm.RIGHT);
            rotateArmHolding(rightArm, 1, attributes.getSwingAmount(), ticks);
            if (attributes.getAttributes().isSitting) {
                rightArm.xRot += 0.6F;
            }
            PartUtil.shift(rightArm, 0.5F, 1.5F, 3);
        } else {
            ModelPart leftArm = model.getForeLeg(HumanoidArm.LEFT);
            rotateArmHolding(leftArm, -1, attributes.getSwingAmount(), ticks);
            if (attributes.getAttributes().isSitting) {
                leftArm.xRot += 0.6F;
            }
            PartUtil.shift(leftArm, -0.5F, 1.5F, 3);
        }
    }

    @Deprecated
    public static boolean islookAngleRight(float limbAngle) {
        return Mth.sin(limbAngle / 20) < 0;
    }
}
