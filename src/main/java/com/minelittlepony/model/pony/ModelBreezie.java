package com.minelittlepony.model.pony;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelBreezie extends ModelBiped {

    ModelRenderer neck;
    ModelRenderer tail;
    ModelRenderer tailStub;
    ModelRenderer leftWing;
    ModelRenderer rightWing;

    public ModelBreezie() {
        textureWidth = 64;
        textureHeight = 64;

        this.bipedHeadwear.showModel = false;

        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.setRotationPoint(0, 0, -4);
        bipedHead.addBox(-3, -6, -3F, 6, 6, 6);
        bipedHead.setTextureOffset(28, 0).addBox(2F, -7F, 1F, 1, 1, 1);
        bipedHead.setTextureOffset(24, 0).addBox(-3F, -7F, 1F, 1, 1, 1);
        bipedHead.setTextureOffset(24, 9).addBox(-1F, -2F, -4F, 2, 2, 1);

        ModelRenderer antenna = new ModelRenderer(this);
        antenna.setTextureOffset(28, 2).addBox(1F, -11F, -2F, 1, 6, 1);
        antenna.setTextureOffset(24, 2).addBox(-2F, -11F, -2F, 1, 6, 1);
        setRotation(antenna, -0.2617994F, 0F, 0F);

        this.bipedHead.addChild(antenna);

        bipedBody = new ModelRenderer(this, 2, 12);
        bipedBody.addBox(0F, 0F, 0F, 6, 7, 14).setRotationPoint(-3F, 1F, -3F);
        setRotation(bipedBody, -0.5235988F, 0F, 0F);

        bipedRightArm = new ModelRenderer(this, 36, 12);
        bipedRightArm.addBox(0F, 0F, 0F, 2, 12, 2).setRotationPoint(-3F, 8F, -5F);

        bipedLeftArm = new ModelRenderer(this, 28, 12);
        bipedLeftArm.addBox(0F, 0F, 0F, 2, 12, 2).setRotationPoint(1F, 8F, -5F);

        bipedLeftLeg = new ModelRenderer(this, 8, 12);
        bipedLeftLeg.addBox(0F, 0F, 0F, 2, 12, 2).setRotationPoint(1F, 12F, 3F);

        bipedRightLeg = new ModelRenderer(this, 0, 12);
        bipedRightLeg.addBox(0F, 0F, 0F, 2, 12, 2).setRotationPoint(-3F, 12F, 3F);

        neck = new ModelRenderer(this, 40, 0);
        neck.addBox(0F, 0F, 0F, 2, 5, 2).setRotationPoint(-1F, -2F, -4F);
        setRotation(neck, 0.0872665F, 0F, 0F);

        tailStub = new ModelRenderer(this, 40, 7);
        tailStub.addBox(0F, 0F, 0F, 1, 1, 3).setRotationPoint(-0.5F, 8F, 8F);

        tail = new ModelRenderer(this, 32, 0);
        tail.addBox(0F, 0F, 1F, 2, 9, 2).setRotationPoint(-1F, 7F, 10F);

        leftWing = new ModelRenderer(this, 0, 40);
        leftWing.addBox(0F, -12F, 0F, 24, 24, 0);
        leftWing.setRotationPoint(2F, 3F, 1F);
        leftWing.setTextureSize(64, 32);
        setRotation(leftWing, 0F, -0.6981317F, 0F);

        rightWing = new ModelRenderer(this, 0, 40);
        rightWing.addBox(-24F, -12F, 0F, 24, 24, 0);
        rightWing.setRotationPoint(-2F, 3F, 1F);
        rightWing.setTextureSize(64, 32);
        rightWing.mirror = true;
        setRotation(rightWing, 0F, 0.6981317F, 0F);

    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        neck.render(f5);
        tailStub.render(f5);
        tail.render(f5);
        leftWing.render(f5);
        rightWing.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

        this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;
        this.bipedHead.rotateAngleX = headPitch * 0.017453292F;

        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;
        this.bipedRightLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.rotateAngleZ = 0.0F;

        if (this.isRiding) {
            this.bipedRightArm.rotateAngleX += -((float) Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float) Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -1.4137167F;
            this.bipedRightLeg.rotateAngleY = ((float) Math.PI / 10F);
            this.bipedRightLeg.rotateAngleZ = 0.07853982F;
            this.bipedLeftLeg.rotateAngleX = -1.4137167F;
            this.bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        this.bipedRightArm.rotateAngleY = 0;
        this.bipedRightArm.rotateAngleZ = 0F;

        switch (this.leftArmPose) {
            case EMPTY:
                this.bipedLeftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedLeftArm.rotateAngleY = 0.5235988F;
                break;
            case ITEM:
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                this.bipedLeftArm.rotateAngleY = 0.0F;
        }

        switch (this.rightArmPose) {
            case EMPTY:
                this.bipedRightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                this.bipedRightArm.rotateAngleY = -0.5235988F;
                break;
            case ITEM:
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                this.bipedRightArm.rotateAngleY = 0.0F;
        }

        if (this.swingProgress > 0.0F) {
            EnumHandSide enumhandside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
            float f1 = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (enumhandside == EnumHandSide.LEFT) {
                this.bipedBody.rotateAngleY *= -1.0F;
            }

            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            //noinspection SuspiciousNameCombination
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) (modelrenderer.rotateAngleX - (f2 * 1.2D + f3));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
        }

        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

        if (this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
        } else if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
            this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
            this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
        }

    }

}
