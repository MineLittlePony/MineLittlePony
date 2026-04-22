package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;

import com.minelittlepony.client.render.entity.AllayRenderer;

public class BreezieModel extends HumanoidModel<AllayRenderer.State> {

    private ModelPart leftWing;
    private ModelPart rightWing;

    public BreezieModel(ModelPart tree) {
        super(tree);
        leftWing = tree.getChild("left_wing");
        rightWing = tree.getChild("right_wing");
    }

    @Override
    public void setupAnim(AllayRenderer.State state) {
        super.setupAnim(state);
        hat.skipDraw = true;

        float move = state.walkAnimationPos;
        float swing = state.walkAnimationSpeed;

        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        head.xRot = state.xRot * Mth.DEG_TO_RAD;

        leftArm.xRot = Mth.cos(move * 0.6662F) * swing;
        leftArm.zRot = 0;

        rightArm.setRotation(swing * Mth.cos(move * 0.6662F + Mth.PI),        0, 0);
        leftLeg .setRotation(swing * Mth.cos(move * 0.6662F + Mth.PI) * 1.4F, 0, 0);
        rightLeg.setRotation(swing * Mth.cos(move * 0.6662F)          * 1.4F, 0, 0);

        if (state.hasPose(Pose.SITTING)) {
            leftArm.xRot += -Mth.PI / 5;
            rightArm.xRot += -Mth.PI / 5;

            rotateLegRiding(leftLeg, -1);
            rotateLegRiding(rightLeg, 1);
        }

        rotateArm(leftArm, state.leftArmPose, 1);
        rotateArm(rightArm, state.rightArmPose, 1);

        if (state.attackTime > 0) {
            swingArms(state, state.mainArm);
        }

        float rotX = Mth.sin(state.ageInTicks * 0.067F) * 0.05F;
        float rotZ = Mth.cos(state.ageInTicks * 0.09F) * 0.05F + 0.05F;

        leftArm.xRot -= rotX;
        leftArm.zRot -= rotZ;

        rightArm.xRot += rotX;
        rightArm.zRot += rotZ;

        rotX = Mth.sin(state.ageInTicks * 0.3F) * 0.05F;
        rotZ = Mth.cos(state.ageInTicks * 0.2F) * 0.05F + 0.05F;

        rotX -= 0.05F;

        leftWing.yRot = rotX * 10;
        leftWing.xRot = rotZ;
        rightWing.yRot = -rotX * 10;
        rightWing.xRot = rotZ;

        if (state.rightArmPose == ArmPose.BOW_AND_ARROW) {
            raiseArm(rightArm, leftArm, -1);
        } else if (state.leftArmPose == ArmPose.BOW_AND_ARROW) {
            raiseArm(leftArm, rightArm, 1);
        }
    }

    protected void rotateLegRiding(ModelPart leg, float factor) {
        leg.setRotation(-1.4137167F, factor * Mth.PI * 0.1F, factor * Mth.PI * 0.025F);
    }

    protected void swingArms(AllayRenderer.State state, HumanoidArm mainHand) {
        body.yRot = Mth.sin(Mth.sqrt(state.attackTime) * Mth.TWO_PI) * 0.2F;

        if (mainHand == HumanoidArm.LEFT) {
            body.yRot *= -1;
        }

        float sin = Mth.sin(body.yRot) * 5;
        float cos = Mth.cos(body.yRot) * 5;

        leftArm.xRot += body.yRot;
        leftArm.yRot += body.yRot;
        leftArm.x = cos;
        leftArm.z = -sin;

        rightArm.yRot += body.yRot;
        rightArm.x = -cos;
        rightArm.z = sin;

        float swingAmount = 1 - (float)Math.pow(1 - state.attackTime, 4);

        float swingFactorX = Mth.sin(swingAmount * Mth.PI);
        float swingX = Mth.sin(state.attackTime * Mth.PI) * (0.7F - head.xRot) * 0.75F;

        ModelPart mainArm = getArm(mainHand);
        mainArm.xRot -= swingFactorX * 1.2F + swingX;
        mainArm.yRot += body.yRot * 2;
        mainArm.zRot -= Mth.sin(state.attackTime * Mth.PI) * 0.4F;
    }

    protected void rotateArm(ModelPart arm, ArmPose pose, float factor) {
        switch (pose) {
            case EMPTY:
                arm.yRot = 0;
                break;
            case ITEM:
                arm.xRot = arm.xRot / 2 - (Mth.PI / 10);
                arm.yRot = 0;
            case BLOCK:
                arm.xRot = arm.xRot / 2 - 0.9424779F;
                arm.yRot = factor * 0.5235988F;
                break;
            default:
        }
    }

    protected void raiseArm(ModelPart up, ModelPart down, float factor) {
        up.yRot = head.yRot + (factor / 10);
        up.xRot = head.xRot - Mth.HALF_PI;

        down.yRot = head.yRot - (factor / 2);
        down.xRot = head.xRot - Mth.HALF_PI;
    }
}
