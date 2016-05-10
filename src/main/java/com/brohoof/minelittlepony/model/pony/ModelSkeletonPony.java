package com.brohoof.minelittlepony.model.pony;

import static net.minecraft.client.renderer.GlStateManager.*;

import net.minecraft.util.math.MathHelper;

public class ModelSkeletonPony extends ModelPlayerPony {

    public ModelSkeletonPony() {
        super(false);
    }

    @Override
    protected void rotateLegs(float move, float swing, float tick) {

        float rightArmRotateAngleX;
        float leftArmRotateAngleX;
        float rightLegRotateAngleX;
        float leftLegRotateAngleX;
        float var8;
        float var9;
        if (this.isFlying && this.metadata.getRace().hasWings()) {
            if (this.rainboom) {
                rightArmRotateAngleX = ROTATE_270;
                leftArmRotateAngleX = ROTATE_270;
                rightLegRotateAngleX = ROTATE_90;
                leftLegRotateAngleX = ROTATE_90;
            } else {
                rightArmRotateAngleX = MathHelper.sin(0.0F - swing * 0.5F);
                leftArmRotateAngleX = MathHelper.sin(0.0F - swing * 0.5F);
                rightLegRotateAngleX = MathHelper.sin(swing * 0.5F);
                leftLegRotateAngleX = MathHelper.sin(swing * 0.5F);
            }

            this.bipedRightArm.rotateAngleY = 0.2F;
            this.steveRightArm.rotateAngleY = 0.2F;
            this.bipedLeftArm.rotateAngleY = -0.2F;
            this.bipedRightLeg.rotateAngleY = -0.2F;
            this.bipedLeftLeg.rotateAngleY = 0.2F;
        } else {
            var8 = (float) Math.pow(swing, 16.0D);
            this.getClass();
            var9 = 3.1415927F * var8 * 0.5F;
            this.getClass();
            float laQuad = 3.1415927F * var8;
            this.getClass();
            float rlQuad = 3.1415927F * var8 * 0.2F;
            this.getClass();
            float llQuad = 3.1415927F * var8 * -0.4F;
            rightArmRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + var9) * 0.6F * swing;
            leftArmRotateAngleX = MathHelper.cos(move * 0.6662F + laQuad) * 0.6F * swing;
            rightLegRotateAngleX = MathHelper.cos(move * 0.6662F + rlQuad) * 0.6F * swing;
            leftLegRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + llQuad) * 0.6F * swing;
            this.bipedRightArm.rotateAngleY = 0.0F;
            this.steveRightArm.rotateAngleY = 0.0F;
            this.unicornarm.rotateAngleY = 0.0F;
            this.bipedLeftArm.rotateAngleY = 0.0F;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;
        }

        this.bipedRightArm.rotateAngleX = rightArmRotateAngleX;
        this.steveRightArm.rotateAngleX = rightArmRotateAngleX;
        this.unicornarm.rotateAngleX = rightArmRotateAngleX;
        this.bipedLeftArm.rotateAngleX = leftArmRotateAngleX;
        this.bipedRightLeg.rotateAngleX = rightLegRotateAngleX;
        this.bipedLeftLeg.rotateAngleX = leftLegRotateAngleX;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.steveRightArm.rotateAngleZ = 0.0F;
        this.unicornarm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        if (this.rightArmPose != ArmPose.EMPTY) {
            var8 = MathHelper.sin(this.swingProgress * 3.1415927F);
            var9 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
            if (this.metadata.getGlowColor() == 0) {
                this.bipedRightArm.rotateAngleZ = 0.0F;
                this.bipedRightArm.rotateAngleY = 0.1F - var8 * 0.6F;
                this.bipedRightArm.rotateAngleX = -1.5707964F;
                this.bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
                this.bipedRightArm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
                this.bipedRightArm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.1F;
            } else {
                this.unicornarm.rotationPointX = -7.0F;
                this.unicornarm.rotationPointY = 12.0F;
                this.unicornarm.rotationPointZ = -2.0F;
                this.unicornarm.rotateAngleZ = 0.0F;
                this.unicornarm.rotateAngleY = 0.1F - var8 * 0.6F;
                this.unicornarm.rotateAngleX = -1.5707964F;
                this.unicornarm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
                this.unicornarm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
                this.unicornarm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.1F;
            }
        }

    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (this.rightArmPose != ArmPose.EMPTY && this.metadata.getGlowColor() == 0) {
            setRotationPoint(this.bipedRightArm, -1.5F, 9.5F, 4.0F);
        }

    }

    @Override
    protected void renderLegs() {
        pushMatrix();
        if (this.leftArmPose != ArmPose.EMPTY && this.metadata.getRace().hasHorn()) {
            translate(0.1F, 0.3F, -0.1F);
            scale(0.5F, 0.5F, 1.2F);
        } else {
            translate(0.05F, -0.21F, -0.0F);
            scale(0.5F, 1.15F, 0.5F);
        }

        this.bipedLeftArm.render(this.scale);
        popMatrix();

        pushMatrix();
        if (this.rightArmPose != ArmPose.EMPTY && this.metadata.getRace().hasHorn()) {
            translate(-0.1F, 0.3F, 0.1F);
            scale(0.5F, 0.5F, 1.2F);
        } else {
            translate(-0.05F, -0.21F, -0.0F);
            scale(0.5F, 1.2F, 0.5F);
        }

        this.bipedRightArm.render(this.scale);
        popMatrix();

        pushMatrix();
        translate(0.05F, -0.21F, 0.35F);
        scale(0.5F, 1.2F, 0.5F);
        this.bipedLeftLeg.render(this.scale);
        popMatrix();

        pushMatrix();
        translate(-0.05F, -0.21F, 0.35F);
        scale(0.5F, 1.15F, 0.5F);
        this.bipedRightLeg.render(this.scale);
        popMatrix();
    }
}
