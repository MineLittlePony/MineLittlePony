package com.minelittlepony.model.pony.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelZombiePonyArmor extends ModelPonyArmor {

    @Override
    protected void rotateLegs(float move, float swing, float tick, Entity entity) {
        float rightArmRotateAngleX;
        float leftArmRotateAngleX;
        float rightLegRotateAngleX;
        float leftLegRotateAngleX;
        float var8;
        float var9;
        if (this.isFlying && this.metadata.getRace().hasWings() || entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isElytraFlying()) {
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
            var9 = 3.1415927F * var8 * 0.5F;
            float laQuad = 3.1415927F * var8;
            float rlQuad = 3.1415927F * var8 * 0.2F;
            float llQuad = 3.1415927F * var8 * -0.4F;
            rightArmRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + var9) * 0.45F * swing;
            leftArmRotateAngleX = MathHelper.cos(move * 0.6662F + laQuad) * 0.45F * swing;
            rightLegRotateAngleX = MathHelper.cos(move * 0.6662F + rlQuad) * 0.45F * swing;
            leftLegRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + llQuad) * 0.45F * swing;
            this.bipedRightArm.rotateAngleY = 0.0F;
            this.steveRightArm.rotateAngleY = 0.0F;
            this.unicornArmRight.rotateAngleY = 0.0F;
            this.bipedLeftArm.rotateAngleY = 0.0F;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;
        }

        this.bipedRightArm.rotateAngleX = rightArmRotateAngleX;
        this.steveRightArm.rotateAngleX = rightArmRotateAngleX;
        this.unicornArmRight.rotateAngleX = 0.0F;
        this.bipedLeftArm.rotateAngleX = leftArmRotateAngleX;
        this.bipedRightLeg.rotateAngleX = rightLegRotateAngleX;
        this.bipedLeftLeg.rotateAngleX = leftLegRotateAngleX;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.steveRightArm.rotateAngleZ = 0.0F;
        this.unicornArmRight.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        if (this.rightArmPose == ArmPose.EMPTY) {
            var8 = MathHelper.sin(this.swingProgress * 3.1415927F);
            var9 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
            if (MathHelper.sin(move / 20.0F) < 0.0F) {
                this.bipedRightArm.rotateAngleZ = 0.0F;
                this.bipedRightArm.rotateAngleY = 0.1F - var8 * 0.6F;
                this.bipedRightArm.rotateAngleX = -1.5707964F;
                this.bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
                this.bipedRightArm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
                this.bipedRightArm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.1F;
            } else {
                this.bipedLeftArm.rotateAngleZ = 0.0F;
                this.bipedLeftArm.rotateAngleY = -(0.1F - var8 * 0.6F);
                this.bipedLeftArm.rotateAngleX = -1.5707964F;
                this.bipedLeftArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
                this.bipedLeftArm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
                this.bipedLeftArm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.1F;
            }
        }

        this.syncLegs();
    }

    @Override
    protected void fixSpecialRotationPoints(float move) {
        if (this.rightArmPose == ArmPose.EMPTY) {
            if (MathHelper.sin(move / 20.0F) < 0.0F) {
                shiftRotationPoint(this.bipedRightArm, 0.5F, 1.5F, 3.0F);
            } else {
                shiftRotationPoint(this.bipedLeftArm, -0.5F, 1.5F, 3.0F);
            }
        }

    }
}
