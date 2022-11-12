package com.minelittlepony.model.ponies;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.render.model.PonyRenderer;

import static com.minelittlepony.model.PonyModelConstants.PI;

public class ModelBreezie extends ModelBiped {

    PonyRenderer neck;
    PonyRenderer tail;
    PonyRenderer tailStub;

    PonyRenderer leftWing;
    PonyRenderer rightWing;

    public ModelBreezie() {
        textureWidth = 64;
        textureHeight = 64;

        bipedHeadwear.showModel = false;
        bipedHead = new PonyRenderer(this)
                .child(new PonyRenderer(this)
                    .addBox(-3, -6, -3, 6, 6, 6).around(0, 0, -4)
                    .tex(28, 0).addBox( 2, -7,  1, 1, 1, 1)
                    .tex(24, 0).addBox(-3, -7,  1, 1, 1, 1)
                    .tex(24, 9).addBox(-1, -2, -4, 2, 2, 1))
                .child(new PonyRenderer(this)
                    .tex(28, 2).addBox( 1, -11, -2, 1, 6, 1)
                    .tex(24, 2).addBox(-2, -11, -2, 1, 6, 1)
                    .rotate(-0.2617994F, 0, 0));

        bipedBody = new PonyRenderer(this, 2, 12)
                .addBox(0, 0, 0, 6, 7, 14).rotate(-0.5235988F, 0, 0).around(-3, 1, -3);

        bipedLeftArm =  new PonyRenderer(this, 28, 12).addBox(0, 0, 0, 2, 12, 2).around( 1, 8, -5);
        bipedRightArm = new PonyRenderer(this, 36, 12).addBox(0, 0, 0, 2, 12, 2).around(-3, 8, -5);
        bipedLeftLeg =  new PonyRenderer(this, 8, 12) .addBox(0, 0, 0, 2, 12, 2).around( 1, 12, 3);
        bipedRightLeg = new PonyRenderer(this, 0, 12) .addBox(0, 0, 0, 2, 12, 2).around(-3, 12, 3);

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
    public void render(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
        neck.render(scale);
        tailStub.render(scale);
        tail.render(scale);
        leftWing.render(scale);
        rightWing.render(scale);
    }

    @Override
    public void setRotationAngles(float move, float swing, float ticks, float headYaw, float headPitch, float scale, Entity entity) {

        bipedHead.rotateAngleY = headYaw * 0.017453292F;
        bipedHead.rotateAngleX = headPitch * 0.017453292F;

        bipedLeftArm.rotateAngleX = MathHelper.cos(move * 0.6662F) * swing;
        bipedLeftArm.rotateAngleZ = 0;

        ((PonyRenderer)bipedRightArm).rotate(swing * MathHelper.cos(move * 0.6662F + PI),        0, 0);
        ((PonyRenderer)bipedLeftLeg) .rotate(swing * MathHelper.cos(move * 0.6662F + PI) * 1.4F, 0, 0);
        ((PonyRenderer)bipedRightLeg).rotate(swing * MathHelper.cos(move * 0.6662F)      * 1.4F, 0, 0);

        if (isRiding) {
            bipedLeftArm.rotateAngleX += -PI / 5;
            bipedRightArm.rotateAngleX += -PI / 5;

            rotateLegRiding(((PonyRenderer)bipedLeftLeg), -1);
            rotateLegRiding(((PonyRenderer)bipedRightLeg), 1);
        }

        rotateArm(bipedLeftArm, leftArmPose, 1);
        rotateArm(bipedRightArm, rightArmPose, 1);

        if (swingProgress > 0) {
            swingArms(getMainHand(entity));
        }

        float rotX = MathHelper.sin(ticks * 0.067F) * 0.05F;
        float rotZ = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;

        bipedLeftArm.rotateAngleX -= rotX;
        bipedLeftArm.rotateAngleZ -= rotZ;

        bipedRightArm.rotateAngleX += rotX;
        bipedRightArm.rotateAngleZ += rotZ;

        if (rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            raiseArm(bipedRightArm, bipedLeftArm, -1);
        } else if (leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
            raiseArm(bipedLeftArm, bipedRightArm, 1);
        }
    }

    protected void rotateLegRiding(PonyRenderer leg, float factor) {
        leg.rotate(-1.4137167F, factor * PI / 10, factor * 0.07853982F);
    }

    protected void swingArms(EnumHandSide mainHand) {
        bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(swingProgress) * PI * 2) / 5;

        if (mainHand == EnumHandSide.LEFT) {
            bipedBody.rotateAngleY *= -1;
        }

        float sin = MathHelper.sin(bipedBody.rotateAngleY) * 5;
        float cos = MathHelper.cos(bipedBody.rotateAngleY) * 5;

        bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
        bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
        bipedLeftArm.rotationPointX = cos;
        bipedLeftArm.rotationPointZ = -sin;

        bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
        bipedRightArm.rotationPointX = -cos;
        bipedRightArm.rotationPointZ = sin;

        float swingAmount = 1 - (float)Math.pow(1 - swingProgress, 4);

        float swingFactorX = MathHelper.sin(swingAmount * PI);
        float swingX = MathHelper.sin(swingProgress * PI) * (0.7F - bipedHead.rotateAngleX) * 0.75F;

        ModelRenderer mainArm = getArmForSide(mainHand);
        mainArm.rotateAngleX -= swingFactorX * 1.2F + swingX;
        mainArm.rotateAngleY += bipedBody.rotateAngleY * 2;
        mainArm.rotateAngleZ -= MathHelper.sin(swingProgress * PI) * 0.4F;
    }

    protected void rotateArm(ModelRenderer arm, ArmPose pose, float factor) {
        switch (pose) {
            case EMPTY:
                arm.rotateAngleY = 0;
                break;
            case ITEM:
                arm.rotateAngleX = arm.rotateAngleX / 2 - (PI / 10);
                arm.rotateAngleY = 0;
            case BLOCK:
                arm.rotateAngleX = arm.rotateAngleX / 2 - 0.9424779F;
                arm.rotateAngleY = factor * 0.5235988F;
                break;
            default:
        }
    }

    protected void raiseArm(ModelRenderer up, ModelRenderer down, float factor) {
        up.rotateAngleY = bipedHead.rotateAngleY + (factor / 10);
        up.rotateAngleX = bipedHead.rotateAngleX - (PI / 2);

        down.rotateAngleY = bipedHead.rotateAngleY - (factor / 2);
        down.rotateAngleX = bipedHead.rotateAngleX - (PI / 2);
    }
}
