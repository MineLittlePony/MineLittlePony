package com.minelittlepony.model.entity;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.pony.ModelPlayerPony;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelMobPony extends ModelPlayerPony {

    public ModelMobPony() {
        super(false);
    }

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
            rightArmRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + var9) * 0.6F * swing;
            leftArmRotateAngleX = MathHelper.cos(move * 0.6662F + laQuad) * 0.6F * swing;
            rightLegRotateAngleX = MathHelper.cos(move * 0.6662F + rlQuad) * 0.6F * swing;
            leftLegRotateAngleX = MathHelper.cos(move * 0.6662F + 3.1415927F + llQuad) * 0.6F * swing;
            this.bipedRightArm.rotateAngleY = 0.0F;
            this.steveRightArm.rotateAngleY = 0.0F;
            this.unicornArmRight.rotateAngleY = 0.0F;
            this.bipedLeftArm.rotateAngleY = 0.0F;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;
        }

        this.bipedRightArm.rotateAngleX = rightArmRotateAngleX;
        this.steveRightArm.rotateAngleX = rightArmRotateAngleX;
        this.unicornArmRight.rotateAngleX = rightArmRotateAngleX;
        this.bipedLeftArm.rotateAngleX = leftArmRotateAngleX;
        this.bipedRightLeg.rotateAngleX = rightLegRotateAngleX;
        this.bipedLeftLeg.rotateAngleX = leftLegRotateAngleX;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.steveRightArm.rotateAngleZ = 0.0F;
        this.unicornArmRight.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        
        var8 = MathHelper.sin(this.swingProgress * (float)Math.PI);
        var9 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
        
        rotateRightArm(var8, var9, move, tick);
        rotateLeftArm(var8, var9, move, tick);
    }
    
    protected void rotateRightArm(float var8, float var9, float move, float tick) {
        if (this.rightArmPose == ArmPose.EMPTY) return;
        
        if (!this.metadata.hasMagic()) {
            rotateArmHolding(bipedRightArm, 1, var8, var9, tick);
        } else {
            this.unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmRight, 1, var8, var9, tick);
        }
    }

    protected static void rotateArmHolding(ModelRenderer arm, float direction, float var8, float var9, float tick) {
        arm.rotateAngleZ = 0.0F;
        arm.rotateAngleY = direction * (0.1F - var8 * 0.6F);
        arm.rotateAngleX = -1.5707964F;
        arm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
        arm.rotateAngleZ += MathHelper.cos(tick * 0.09F) * 0.05F + 0.05F;
        arm.rotateAngleX += MathHelper.sin(tick * 0.067F) * 0.1F;
    }
    
    
    protected void rotateLeftArm(float var8, float var9, float move, float tick) {
        if (this.leftArmPose == ArmPose.EMPTY) return;

        if (!this.metadata.hasMagic()) {
            rotateArmHolding(bipedLeftArm, 1, var8, var9, tick);
        } else {
            this.unicornArmRight.setRotationPoint(-7, 12, -2);
            rotateArmHolding(unicornArmLeft, 1, var8, var9, tick);
        }
    }
}
