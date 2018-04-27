package com.minelittlepony.model.ponies;

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

        bipedHeadwear.showModel = false;

        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.setRotationPoint(0, 0, -4);
        bipedHead.addBox(-3, -6, -3F, 6, 6, 6);
        bipedHead.setTextureOffset(28, 0).addBox(2F, -7F, 1F, 1, 1, 1);
        bipedHead.setTextureOffset(24, 0).addBox(-3F, -7F, 1F, 1, 1, 1);
        bipedHead.setTextureOffset(24, 9).addBox(-1F, -2F, -4F, 2, 2, 1);

        ModelRenderer antenna = new ModelRenderer(this);
        antenna.setTextureOffset(28, 2).addBox(1F, -11F, -2F, 1, 6, 1);
        antenna.setTextureOffset(24, 2).addBox(-2F, -11F, -2F, 1, 6, 1);
        setRotation(antenna, -0.2617994F, 0, 0);

        bipedHead.addChild(antenna);

        bipedBody = new ModelRenderer(this, 2, 12);
        bipedBody.addBox(0, 0, 0, 6, 7, 14).setRotationPoint(-3, 1, -3);
        setRotation(bipedBody, -0.5235988F, 0, 0);

        bipedRightArm = new ModelRenderer(this, 36, 12);
        bipedRightArm.addBox(0, 0, 0, 2, 12, 2).setRotationPoint(-3, 8, -5);

        bipedLeftArm = new ModelRenderer(this, 28, 12);
        bipedLeftArm.addBox(0, 0, 0, 2, 12, 2).setRotationPoint(1, 8, -5);

        bipedLeftLeg = new ModelRenderer(this, 8, 12);
        bipedLeftLeg.addBox(0, 0, 0, 2, 12, 2).setRotationPoint(1, 12, 3);

        bipedRightLeg = new ModelRenderer(this, 0, 12);
        bipedRightLeg.addBox(0, 0, 0, 2, 12, 2).setRotationPoint(-3, 12, 3);

        neck = new ModelRenderer(this, 40, 0);
        neck.addBox(0, 0, 0, 2, 5, 2).setRotationPoint(-1, -2, -4);
        setRotation(neck, 0.0872665F, 0, 0);

        tailStub = new ModelRenderer(this, 40, 7);
        tailStub.addBox(0, 0, 0, 1, 1, 3).setRotationPoint(-0.5F, 8, 8);

        tail = new ModelRenderer(this, 32, 0);
        tail.addBox(0, 0, 1, 2, 9, 2).setRotationPoint(-1, 7, 10);

        leftWing = new ModelRenderer(this, 0, 40);
        leftWing.addBox(0, -12, 0, 24, 24, 0);
        leftWing.setRotationPoint(2, 3, 1);
        leftWing.setTextureSize(64, 32);
        setRotation(leftWing, 0, -0.6981317F, 0);

        rightWing = new ModelRenderer(this, 0, 40);
        rightWing.addBox(-24, -12, 0, 24, 24, 0, true);
        rightWing.setRotationPoint(-2, 3, 1);
        rightWing.setTextureSize(64, 32);
        setRotation(rightWing, 0, 0.6981317F, 0);

    }

    @Override
    public void render(Entity entity, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        super.render(entity, move, swing, age, headYaw, headPitch, scale);
        neck.render(scale);
        tailStub.render(scale);
        tail.render(scale);
        leftWing.render(scale);
        rightWing.render(scale);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {

        bipedHead.rotateAngleY = headYaw * 0.017453292F;
        bipedHead.rotateAngleX = headPitch * 0.017453292F;

        bipedRightArm.rotateAngleX = MathHelper.cos(move * 0.6662F + (float) Math.PI) * 2.0F * swing * 0.5F;
        bipedLeftArm.rotateAngleX = MathHelper.cos(move * 0.6662F) * 2.0F * swing * 0.5F;
        bipedRightArm.rotateAngleZ = 0;
        bipedLeftArm.rotateAngleZ = 0;
        bipedRightLeg.rotateAngleX = MathHelper.cos(move * 0.6662F) * 1.4F * swing;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(move * 0.6662F + (float) Math.PI) * 1.4F * swing;
        bipedRightLeg.rotateAngleY = 0;
        bipedLeftLeg.rotateAngleY = 0;
        bipedRightLeg.rotateAngleZ = 0;
        bipedLeftLeg.rotateAngleZ = 0;

        if (isRiding) {
            bipedRightArm.rotateAngleX += -((float) Math.PI / 5F);
            bipedLeftArm.rotateAngleX += -((float) Math.PI / 5F);
            bipedRightLeg.rotateAngleX = -1.4137167F;
            bipedRightLeg.rotateAngleY = ((float) Math.PI / 10F);
            bipedRightLeg.rotateAngleZ = 0.07853982F;
            bipedLeftLeg.rotateAngleX = -1.4137167F;
            bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);
            bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        bipedRightArm.rotateAngleY = 0;
        bipedRightArm.rotateAngleZ = 0F;

        switch (leftArmPose) {
            case EMPTY:
                bipedLeftArm.rotateAngleY = 0;
                break;
            case BLOCK:
                bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                bipedLeftArm.rotateAngleY = 0.5235988F;
                break;
            case ITEM:
                bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                bipedLeftArm.rotateAngleY = 0;
        }

        switch (rightArmPose) {
            case EMPTY:
                bipedRightArm.rotateAngleY = 0;
                break;
            case BLOCK:
                bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                bipedRightArm.rotateAngleY = -0.5235988F;
                break;
            case ITEM:
                bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                bipedRightArm.rotateAngleY = 0;
        }

        if (swingProgress > 0.0F) {
            EnumHandSide enumhandside = getMainHand(entity);
            ModelRenderer modelrenderer = getArmForSide(enumhandside);
            float f1 = swingProgress;
            bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (enumhandside == EnumHandSide.LEFT) {
                bipedBody.rotateAngleY *= -1.0F;
            }

            bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5;
            bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5;
            bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5;
            bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            //noinspection SuspiciousNameCombination
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            f1 = 1.0F - swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(swingProgress * (float) Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) (modelrenderer.rotateAngleX - (f2 * 1.2D + f3));
            modelrenderer.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(swingProgress * (float) Math.PI) * -0.4F;
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(age * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(age * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(age * 0.067F) * 0.05F;

        if (rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY;
            bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
        } else if (leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - 0.4F;
            bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2) + bipedHead.rotateAngleX;
        }

    }

}
