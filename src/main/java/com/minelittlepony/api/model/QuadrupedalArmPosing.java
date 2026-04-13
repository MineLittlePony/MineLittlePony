package com.minelittlepony.api.model;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.Sigma;

public interface QuadrupedalArmPosing {
    static @Sigma float sigmaOf(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? Sigma.LEFT : Sigma.RIGHT;
    }
    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void punch(T state, ModelPart arm, ModelPart body, ModelPart head) {
        float swing = 1 - (float)Math.pow(1 - state.attackTime, 3);

        float deltaX = Mth.sin(swing * Mth.PI);
        float deltaZ = Mth.sin(state.attackTime * Mth.PI);

        float deltaAim = deltaZ * (0.7F - head.xRot) * 0.75F;

        arm.xRot -= deltaAim + deltaX * 1.2F;
        arm.yRot += body.yRot * 2;
        arm.zRot = -deltaZ * 0.4F;
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void holdItem(T state, ModelPart arm, ArmPose pose, ArmPose complement, HumanoidArm side) {
        @Sigma float sigma = sigmaOf(side);
        arm.yRot = 0;

        boolean both = pose == complement;

        if (state.getAttributes().shouldLiftArm(pose, complement, sigma)) {
            float swag = 1;
            if (!state.getAttributes().isFlying && both) {
                swag -= (float)Math.pow(state.walkAnimationSpeed, 2);
            }

            float mult = 1 - swag/2;
            arm.xRot = arm.xRot * mult - (Mth.PI / 10) * swag;
            arm.zRot = -sigma * (Mth.PI / 15);
            arm.zRot += 0.3F * -state.walkAnimationSpeed * sigma;

            if (state.getAttributes().isCrouching) {
                arm.x -= sigma * 2;
            }
        }
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void holdShield(T state, ModelPart arm, ArmPose pose, ArmPose complement, HumanoidArm side) {
        @Sigma float sigma = sigmaOf(side);
        arm.xRot = (arm.xRot / 2 - 0.9424779F) - 0.3F;
        arm.yRot = sigma * Mth.PI / 9;
        arm.zRot += 0.3F * -state.walkAnimationSpeed * sigma;
        if (complement == pose) {
            arm.yRot -= sigma * Mth.PI / 18;
        }
        arm.x += sigma;
        arm.z += 3;
        if (state.getAttributes().isCrouching) {
            arm.y += 4;
        }
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void aimBow(T state, ModelPart head, ModelPart arm) {
        arm.xRot = MathUtil.Angles._270_DEG + head.xRot + (Mth.sin(state.walkAnimationPos * 0.067F) * 0.05F);
        arm.yRot = head.yRot - 0.06F;
        arm.zRot = Mth.cos(state.walkAnimationPos * 0.09F) * 0.05F + 0.05F;

        if (state.isCrouching) {
            arm.y += 4;
        }
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void aimCrossbow(T state, ModelPart head, ModelPart arm, boolean charged, HumanoidArm side) {
        aimBow(state, head, arm);
        if (charged) {
            arm.xRot = -0.8F;
            arm.yRot = head.yRot + 0.06F;
            arm.zRot += 0.3F * -state.walkAnimationPos * sigmaOf(side);
        } else {
            arm.zRot = head.zRot - MathUtil.Angles._90_DEG;
            arm.yRot = head.yRot + 0.06F;
        }
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void spyglass(T state, ModelPart head, ModelPart arm, HumanoidArm side) {
        float addedPitch = state.isCrouching ? -0.2617994F : 0;
        float minPitch = state.isCrouching ? -1.8F : -2.4F;
        arm.xRot = Mth.clamp(head.xRot - 1.9198622F - addedPitch, minPitch, 3.3F);
        arm.yRot = head.yRot;

        if (state.isCrouching) {
            arm.y += 9;
            arm.x -= 6 * sigmaOf(side);
            arm.z -= 2;
        }
        if (state.getAttributes().size == SizePreset.TALL) {
            arm.y += 1;
        }
        if (state.getAttributes().size == SizePreset.FOAL) {
            arm.y -= 2;
        }
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void throwTrident(T state, ModelPart arm, HumanoidArm side) {
        arm.xRot = MathUtil.Angles._90_DEG * 2;
        arm.zRot += (0.3F * -state.walkAnimationPos + 0.6F) * sigmaOf(side);
        arm.y ++;
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void blowHorn(T state, ModelPart head, ModelPart arm, HumanoidArm side) {
        @Sigma float sigma = sigmaOf(side);
        arm.xRot = Mth.clamp(head.xRot, -0.55f, 1.2f) - 1.7835298f;
        arm.yRot = head.yRot - 0.1235988f * sigma;
        arm.y += 3;
        arm.zRot += 0.3F * -state.walkAnimationPos * sigma;
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void brushBlock(T state, ModelPart arm, HumanoidArm side) {
        arm.xRot = arm.xRot * 0.5f - 0.62831855f;
        arm.yRot = 0;
        arm.zRot += 0.3F * -state.walkAnimationPos * sigmaOf(side);
    }

    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void idle(T state, ModelPart leftArm, ModelPart rightArm) {
        float cos = Mth.cos(state.ageInTicks * 0.09F) * 0.05F + 0.05F;
        float sin = Mth.sin(state.ageInTicks * 0.067F) * 0.05F;

        if (state.getAttributes().shouldLiftArm(state.leftArmPose, state.rightArmPose, 1)) {
            leftArm.zRot += cos;
            leftArm.xRot += sin;
        }

        if (state.getAttributes().shouldLiftArm(state.rightArmPose, state.leftArmPose, -1)) {
            rightArm.zRot += cos;
            rightArm.xRot += sin;
        }
    }

}
