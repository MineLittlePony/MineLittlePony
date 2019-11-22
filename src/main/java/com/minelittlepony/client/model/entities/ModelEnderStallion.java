package com.minelittlepony.client.model.entities;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.components.PonySnout;
import com.minelittlepony.client.util.render.Part;

import com.mojang.blaze3d.platform.GlStateManager;

public class ModelEnderStallion extends ModelSkeletonPony<EndermanEntity> {

    public boolean isCarrying;
    public boolean isAttacking;

    public boolean isAlicorn;
    public boolean isBoss;

    private Part leftHorn;
    private Part rightHorn;

    public ModelEnderStallion() {
        super();
        attributes.armRotationX = 3;
        attributes.armRotationY = 14;
        attributes.armLength = 30;
        attributes.visualHeight = 3;
    }

    @Override
    public void animateModel(EndermanEntity entity, float move, float swing, float ticks) {
        rightArmPose = isCarrying ? ArmPose.BLOCK : ArmPose.EMPTY;
        leftArmPose = rightArmPose;

        isUnicorn = true;
        isAlicorn = entity.getUuid().getLeastSignificantBits() % 3 == 0;
        isBoss = !isAlicorn && entity.getUuid().getLeastSignificantBits() % 90 == 0;

        leftHorn.field_3664 = rightHorn.field_3664 = !isBoss;
        horn.setVisible(!isBoss);

        tail.setVisible(false);
        snout.isHidden = true;
        leftSleeve.field_3664 = true;
        rightSleeve.field_3664 = true;

        leftPantLeg.field_3664 = true;
        rightPantLeg.field_3664 = true;

        leftHorn.pitch = 0.5F;
        rightHorn.pitch = 0.5F;
    }

    @Override
    public void setAngles(EndermanEntity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (isAttacking) {
            head.pivotY -= 5;
        }
    }

    @Override
    public void render(EndermanEntity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0, -1.15F, 0);
        super.render(entity, move, swing, ticks, headYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean canFly() {
        return isAlicorn;
    }

    @Override
    protected void initHead(float yOffset, float stretch) {
        head = new Part(this, 0, 0)
                                 .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch)
                     .tex(12, 16).box(-4, -6, 1, 2, 2, 2, stretch)
                          .flip().box( 2, -6, 1, 2, 2, 2, stretch);

        leftHorn = ((Part)head).child().tex(0, 52);
        leftHorn.tex(0, 52)
                .rotate(0.1F, 0, -0.8F)
                .offset(-2, -10, -3)
                .box(0, 0, 0, 2, 6, 2, stretch)
                .child()
                    .rotate(0, 0, 0.9F)
                    .around(-3.9F, -6, 0.001F)
                    .box(0, 0, 0, 2, 6, 2, stretch);

        rightHorn = ((Part)head).child().tex(0, 52);
        rightHorn.tex(8, 52)
                .rotate(0.1F, 0, 0.8F)
                .offset(0, -10, -3)
                .box(0, 0, 0, 2, 6, 2, stretch)
                .child()
                    .rotate(0, 0, -0.9F)
                    .around(3.9F, -6, 0.001F)
                    .box(0, 0, 0, 2, 6, 2, stretch);

        helmet = new Part(this, 32, 0)
                                     .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                     .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                     .box(-4, -4, -4, 8, 8, 8, stretch - 0.5F);

        snout = new PonySnout(this);
        snout.init(yOffset, stretch);
    }

    @Override
    public void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        arm.pitch = -0.3707964F;
        arm.pitch += 0.4F + MathHelper.sin(ticks * 0.067F) / 10;
    }

    @Override
    protected void preInitLegs() {
        leftArm = new ModelPart(this, 0, 20);
        rightArm = new ModelPart(this, 0, 20);

        leftLeg = new ModelPart(this, 0, 20);
        rightLeg = new ModelPart(this, 0, 20);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        leftHorn.visible = visible;
        rightHorn.visible = visible;
    }

    @Override
    public boolean wingsAreOpen() {
        return isAttacking;
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        return MathHelper.sin(ticks) + WING_ROT_Z_SNEAK;
    }
}
