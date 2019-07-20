package com.minelittlepony.client.model.entities;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.util.render.PonyRenderer;

import static com.minelittlepony.model.PonyModelConstants.PI;

public class ModelBreezie<T extends LivingEntity> extends BipedEntityModel<T> {

    PonyRenderer neck;
    PonyRenderer tail;
    PonyRenderer tailStub;

    PonyRenderer leftWing;
    PonyRenderer rightWing;

    public ModelBreezie() {
        textureWidth = 64;
        textureHeight = 64;

        headwear.visible = false;
        head = new PonyRenderer(this)
                .child(new PonyRenderer(this)
                    .addBox(-3, -6, -3, 6, 6, 6).around(0, 0, -4)
                    .tex(28, 0).addBox( 2, -7,  1, 1, 1, 1)
                    .tex(24, 0).addBox(-3, -7,  1, 1, 1, 1)
                    .tex(24, 9).addBox(-1, -2, -4, 2, 2, 1))
                .child(new PonyRenderer(this)
                    .tex(28, 2).addBox( 1, -11, -2, 1, 6, 1)
                    .tex(24, 2).addBox(-2, -11, -2, 1, 6, 1)
                    .rotate(-0.2617994F, 0, 0));

        body = new PonyRenderer(this, 2, 12)
                .addBox(0, 0, 0, 6, 7, 14).rotate(-0.5235988F, 0, 0).around(-3, 1, -3);

        leftArm =  new PonyRenderer(this, 28, 12).addBox(0, 0, 0, 2, 12, 2).around( 1, 8, -5);
        rightArm = new PonyRenderer(this, 36, 12).addBox(0, 0, 0, 2, 12, 2).around(-3, 8, -5);
        leftLeg =  new PonyRenderer(this, 8, 12) .addBox(0, 0, 0, 2, 12, 2).around( 1, 12, 3);
        rightLeg = new PonyRenderer(this, 0, 12) .addBox(0, 0, 0, 2, 12, 2).around(-3, 12, 3);

        neck = new PonyRenderer(this, 40, 0)
                .addBox(0, 0, 0, 2, 5, 2)
                .rotate(0.0872665F, 0, 0).around(-1, -2, -4);

        tailStub = new PonyRenderer(this, 40, 7)
                .addBox(0, 0, 0, 1, 1, 3).around(-0.5F, 8, 8);

        tail = new PonyRenderer(this, 32, 0)
                .addBox(0, 0, 1, 2, 9, 2).around(-1, 7, 10);

        leftWing = new PonyRenderer(this, 0, 40)
                .addBox(0, -12, 0, 24, 24, 0)
                .rotate(0, -0.6981317F, 0).around(2, 3, 1);
        leftWing.setTextureSize(64, 32);

        rightWing = new PonyRenderer(this, 0, 40)
                .addBox(-24, -12, 0, 24, 24, 0, true)
                .rotate(0, 0.6981317F, 0).around(-2, 3, 1);
        rightWing.setTextureSize(64, 32);

    }

    @Override
    public void render(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
        neck.render(scale);
        tailStub.render(scale);
        tail.render(scale);
        leftWing.render(scale);
        rightWing.render(scale);
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {

        head.yaw = headYaw * 0.017453292F;
        head.pitch = headPitch * 0.017453292F;

        leftArm.pitch = MathHelper.cos(move * 0.6662F) * swing;
        leftArm.roll = 0;

        ((PonyRenderer)rightArm).rotate(swing * MathHelper.cos(move * 0.6662F + PI),        0, 0);
        ((PonyRenderer)leftLeg) .rotate(swing * MathHelper.cos(move * 0.6662F + PI) * 1.4F, 0, 0);
        ((PonyRenderer)rightLeg).rotate(swing * MathHelper.cos(move * 0.6662F)      * 1.4F, 0, 0);

        if (isRiding) {
            leftArm.pitch += -PI / 5;
            rightArm.pitch += -PI / 5;

            rotateLegRiding(((PonyRenderer)leftLeg), -1);
            rotateLegRiding(((PonyRenderer)rightLeg), 1);
        }

        rotateArm(leftArm, leftArmPose, 1);
        rotateArm(rightArm, rightArmPose, 1);

        if (handSwingProgress > 0) {
            swingArms(getPreferredArm(entity));
        }

        float rotX = MathHelper.sin(ticks * 0.067F) * 0.05F;
        float rotZ = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;

        leftArm.pitch -= rotX;
        leftArm.roll -= rotZ;

        rightArm.pitch += rotX;
        rightArm.roll += rotZ;

        if (rightArmPose == ArmPose.BOW_AND_ARROW) {
            raiseArm(rightArm, leftArm, -1);
        } else if (leftArmPose == ArmPose.BOW_AND_ARROW) {
            raiseArm(leftArm, rightArm, 1);
        }
    }

    protected void rotateLegRiding(PonyRenderer leg, float factor) {
        leg.rotate(-1.4137167F, factor * PI / 10, factor * 0.07853982F);
    }

    protected void swingArms(Arm mainHand) {
        body.yaw = MathHelper.sin(MathHelper.sqrt(handSwingProgress) * PI * 2) / 5;

        if (mainHand == Arm.LEFT) {
            body.yaw *= -1;
        }

        float sin = MathHelper.sin(body.yaw) * 5;
        float cos = MathHelper.cos(body.yaw) * 5;

        leftArm.pitch += body.yaw;
        leftArm.yaw += body.yaw;
        leftArm.rotationPointX = cos;
        leftArm.rotationPointZ = -sin;

        rightArm.yaw += body.yaw;
        rightArm.rotationPointX = -cos;
        rightArm.rotationPointZ = sin;

        float swingAmount = 1 - (float)Math.pow(1 - handSwingProgress, 4);

        float swingFactorX = MathHelper.sin(swingAmount * PI);
        float swingX = MathHelper.sin(handSwingProgress * PI) * (0.7F - head.pitch) * 0.75F;

        Cuboid mainArm = getArm(mainHand);
        mainArm.pitch -= swingFactorX * 1.2F + swingX;
        mainArm.yaw += body.yaw * 2;
        mainArm.roll -= MathHelper.sin(handSwingProgress * PI) * 0.4F;
    }

    protected void rotateArm(Cuboid arm, ArmPose pose, float factor) {
        switch (pose) {
            case EMPTY:
                arm.yaw = 0;
                break;
            case ITEM:
                arm.pitch = arm.pitch / 2 - (PI / 10);
                arm.yaw = 0;
            case BLOCK:
                arm.pitch = arm.pitch / 2 - 0.9424779F;
                arm.yaw = factor * 0.5235988F;
                break;
            default:
        }
    }

    protected void raiseArm(Cuboid up, Cuboid down, float factor) {
        up.yaw = head.yaw + (factor / 10);
        up.pitch = head.pitch - (PI / 2);

        down.yaw = head.yaw - (factor / 2);
        down.pitch = head.pitch - (PI / 2);
    }
}
