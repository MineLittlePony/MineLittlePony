package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.render.entity.AllayRenderer;

public class BreezieModel extends BipedEntityModel<AllayRenderer.State> {

    private ModelPart leftWing;
    private ModelPart rightWing;

    public BreezieModel(ModelPart tree) {
        super(tree);
        leftWing = tree.getChild("left_wing");
        rightWing = tree.getChild("right_wing");
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        hat.visible = false;
    }

    @Override
    public void setAngles(AllayRenderer.State state) {

        float move = state.limbSwingAnimationProgress;
        float swing = state.limbSwingAmplitude;

        head.yaw = state.relativeHeadYaw * 0.017453292F;
        head.pitch = state.pitch * 0.017453292F;

        leftArm.pitch = MathHelper.cos(move * 0.6662F) * swing;
        leftArm.roll = 0;

        rightArm.setAngles(swing * MathHelper.cos(move * 0.6662F + MathHelper.PI),        0, 0);
        leftLeg .setAngles(swing * MathHelper.cos(move * 0.6662F + MathHelper.PI) * 1.4F, 0, 0);
        rightLeg.setAngles(swing * MathHelper.cos(move * 0.6662F)                 * 1.4F, 0, 0);

        if (state.isInPose(EntityPose.SITTING)) {
            leftArm.pitch += -MathHelper.PI / 5;
            rightArm.pitch += -MathHelper.PI / 5;

            rotateLegRiding(leftLeg, -1);
            rotateLegRiding(rightLeg, 1);
        }

        rotateArm(leftArm, state.leftArmPose, 1);
        rotateArm(rightArm, state.rightArmPose, 1);

        if (state.handSwingProgress > 0) {
            swingArms(state, state.preferredArm);
        }

        float rotX = MathHelper.sin(state.age * 0.067F) * 0.05F;
        float rotZ = MathHelper.cos(state.age * 0.09F) * 0.05F + 0.05F;

        leftArm.pitch -= rotX;
        leftArm.roll -= rotZ;

        rightArm.pitch += rotX;
        rightArm.roll += rotZ;

        rotX = MathHelper.sin(state.age * 0.3F) * 0.05F;
        rotZ = MathHelper.cos(state.age * 0.2F) * 0.05F + 0.05F;

        rotX -= 0.05F;

        leftWing.yaw = rotX * 10;
        leftWing.pitch = rotZ;
        rightWing.yaw = -rotX * 10;
        rightWing.pitch = rotZ;

        if (state.rightArmPose == ArmPose.BOW_AND_ARROW) {
            raiseArm(rightArm, leftArm, -1);
        } else if (state.leftArmPose == ArmPose.BOW_AND_ARROW) {
            raiseArm(leftArm, rightArm, 1);
        }
    }

    protected void rotateLegRiding(ModelPart leg, float factor) {
        leg.setAngles(-1.4137167F, factor * MathHelper.PI / 10, factor * 0.07853982F);
    }

    protected void swingArms(AllayRenderer.State state, Arm mainHand) {
        body.yaw = MathHelper.sin(MathHelper.sqrt(state.handSwingProgress) * MathHelper.TAU) / 5;

        if (mainHand == Arm.LEFT) {
            body.yaw *= -1;
        }

        float sin = MathHelper.sin(body.yaw) * 5;
        float cos = MathHelper.cos(body.yaw) * 5;

        leftArm.pitch += body.yaw;
        leftArm.yaw += body.yaw;
        leftArm.originX = cos;
        leftArm.originZ = -sin;

        rightArm.yaw += body.yaw;
        rightArm.originX = -cos;
        rightArm.originZ = sin;

        float swingAmount = 1 - (float)Math.pow(1 - state.handSwingProgress, 4);

        float swingFactorX = MathHelper.sin(swingAmount * MathHelper.PI);
        float swingX = MathHelper.sin(state.handSwingProgress * MathHelper.PI) * (0.7F - head.pitch) * 0.75F;

        ModelPart mainArm = getArm(mainHand);
        mainArm.pitch -= swingFactorX * 1.2F + swingX;
        mainArm.yaw += body.yaw * 2;
        mainArm.roll -= MathHelper.sin(state.handSwingProgress * MathHelper.PI) * 0.4F;
    }

    protected void rotateArm(ModelPart arm, ArmPose pose, float factor) {
        switch (pose) {
            case EMPTY:
                arm.yaw = 0;
                break;
            case ITEM:
                arm.pitch = arm.pitch / 2 - (MathHelper.PI / 10);
                arm.yaw = 0;
            case BLOCK:
                arm.pitch = arm.pitch / 2 - 0.9424779F;
                arm.yaw = factor * 0.5235988F;
                break;
            default:
        }
    }

    protected void raiseArm(ModelPart up, ModelPart down, float factor) {
        up.yaw = head.yaw + (factor / 10);
        up.pitch = head.pitch - MathHelper.HALF_PI;

        down.yaw = head.yaw - (factor / 2);
        down.pitch = head.pitch - MathHelper.HALF_PI;
    }
}
