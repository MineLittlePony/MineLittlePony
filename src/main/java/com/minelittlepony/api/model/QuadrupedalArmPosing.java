package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.util.MathUtil;
import com.minelittlepony.util.Sigma;

public interface QuadrupedalArmPosing {
    static @Sigma float sigmaOf(Arm arm) {
        return arm == Arm.LEFT ? Sigma.LEFT : Sigma.RIGHT;
    }
    /**
     * Animates arm swinging.
     *
     * @param arm       The arm to swing
     */
    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void punch(T state, ModelPart arm, ModelPart body, ModelPart head) {
        float swing = 1 - (float)Math.pow(1 - state.handSwingProgress, 3);

        float deltaX = MathHelper.sin(swing * MathHelper.PI);
        float deltaZ = MathHelper.sin(state.handSwingProgress * MathHelper.PI);

        float deltaAim = deltaZ * (0.7F - head.pitch) * 0.75F;

        arm.pitch -= deltaAim + deltaX * 1.2F;
        arm.yaw += body.yaw * 2;
        arm.roll = -deltaZ * 0.4F;
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void holdItem(T state, ModelPart arm, ArmPose pose, ArmPose complement, Arm side) {
        @Sigma float sigma = sigmaOf(side);
        arm.yaw = 0;

        boolean both = pose == complement;

        if (state.getAttributes().shouldLiftArm(pose, complement, sigma)) {
            float swag = 1;
            if (!state.getAttributes().isFlying && both) {
                swag -= (float)Math.pow(state.limbSwingAmplitude, 2);
            }

            float mult = 1 - swag/2;
            arm.pitch = arm.pitch * mult - (MathHelper.PI / 10) * swag;
            arm.yaw = -sigma * (MathHelper.PI / 15);
            arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;

            if (state.getAttributes().isCrouching) {
                arm.originX -= sigma * 2;
            }
        }
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void holdShield(T state, ModelPart arm, ArmPose pose, ArmPose complement, Arm side) {
        @Sigma float sigma = sigmaOf(side);
        arm.pitch = (arm.pitch / 2 - 0.9424779F) - 0.3F;
        arm.yaw = sigma * MathHelper.PI / 9;
        arm.roll += 0.3F * -state.limbSwingAnimationProgress * sigma;
        if (complement == pose) {
            arm.yaw -= sigma * MathHelper.PI / 18;
        }
        arm.originX += sigma;
        arm.originZ += 3;
        if (state.getAttributes().isCrouching) {
            arm.originY += 4;
        }
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void aimBow(T state, ModelPart head, ModelPart arm) {
        arm.pitch = MathUtil.Angles._270_DEG + head.pitch + (MathHelper.sin(state.limbSwingAmplitude * 0.067F) * 0.05F);
        arm.yaw = head.yaw - 0.06F;
        arm.roll = MathHelper.cos(state.limbSwingAmplitude * 0.09F) * 0.05F + 0.05F;

        if (state.sneaking) {
            arm.originY += 4;
        }
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void aimCrossbow(T state, ModelPart head, ModelPart arm, boolean charged, Arm side) {
        aimBow(state, head, arm);
        if (charged) {
            arm.pitch = -0.8F;
            arm.yaw = head.yaw + 0.06F;
            arm.roll += 0.3F * -state.limbSwingAmplitude * sigmaOf(side);
        } else {
            arm.roll = head.roll - MathUtil.Angles._90_DEG;
            arm.yaw = head.yaw + 0.06F;
        }
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void spyglass(T state, ModelPart head, ModelPart arm, Arm side) {
        float addedPitch = state.sneaking ? -0.2617994F : 0;
        float minPitch = state.sneaking ? -1.8F : -2.4F;
        arm.pitch = MathHelper.clamp(head.pitch - 1.9198622F - addedPitch, minPitch, 3.3F);
        arm.yaw = head.yaw;

        if (state.sneaking) {
            arm.originY += 9;
            arm.originX -= 6 * sigmaOf(side);
            arm.originZ -= 2;
        }
        if (state.getAttributes().size == SizePreset.TALL) {
            arm.originY += 1;
        }
        if (state.getAttributes().size == SizePreset.FOAL) {
            arm.originY -= 2;
        }
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void throwTrident(T state, ModelPart arm, Arm side) {
        arm.pitch = MathUtil.Angles._90_DEG * 2;
        arm.roll += (0.3F * -state.limbSwingAmplitude + 0.6F) * sigmaOf(side);
        arm.originY ++;
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void blowHorn(T state, ModelPart head, ModelPart arm, Arm side) {
        @Sigma float sigma = sigmaOf(side);
        arm.pitch = MathHelper.clamp(head.pitch, -0.55f, 1.2f) - 1.7835298f;
        arm.yaw = head.yaw - 0.1235988f * sigma;
        arm.originY += 3;
        arm.roll += 0.3F * -state.limbSwingAmplitude * sigma;
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void brushBlock(T state, ModelPart arm, Arm side) {
        arm.pitch = arm.pitch * 0.5f - 0.62831855f;
        arm.yaw = 0;
        arm.roll += 0.3F * -state.limbSwingAmplitude * sigmaOf(side);
    }

    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void idle(T state, ModelPart leftArm, ModelPart rightArm) {
        float cos = MathHelper.cos(state.age * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(state.age * 0.067F) * 0.05F;

        if (state.getAttributes().shouldLiftArm(state.leftArmPose, state.rightArmPose, 1)) {
            leftArm.roll += cos;
            leftArm.pitch += sin;
        }

        if (state.getAttributes().shouldLiftArm(state.rightArmPose, state.leftArmPose, -1)) {
            rightArm.roll += cos;
            rightArm.pitch += sin;
        }
    }

}
