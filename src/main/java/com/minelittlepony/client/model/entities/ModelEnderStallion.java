package com.minelittlepony.client.model.entities;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.components.PonySnout;
import com.minelittlepony.client.util.render.PonyRenderer;

import com.mojang.blaze3d.platform.GlStateManager;

public class ModelEnderStallion extends ModelSkeletonPony<EndermanEntity> {

    public boolean isCarrying;
    public boolean isAttacking;

    public boolean isAlicorn;
    public boolean isBoss;

    private PonyRenderer leftHorn;
    private PonyRenderer rightHorn;

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
        leftArmOverlay.field_3664 = true;
        rightArmOverlay.field_3664 = true;

        leftLegOverlay.field_3664 = true;
        rightLegOverlay.field_3664 = true;

        leftHorn.pitch = 0.5F;
        rightHorn.pitch = 0.5F;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

    }

    @Override
    public void setAngles(EndermanEntity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (isAttacking) {
            head.rotationPointY -= 5;
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
        head = new PonyRenderer(this, 0, 0)
                                 .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                 .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                 .box(-4, -4, -4, 8, 8, 8, stretch)
                     .tex(12, 16).box(-4, -6, 1, 2, 2, 2, stretch)
                          .flip().box( 2, -6, 1, 2, 2, 2, stretch);

        leftHorn = ((PonyRenderer)head).child().tex(0, 52);
        leftHorn.tex(0, 52)
                .rotate(0.1F, 0, -0.8F)
                .offset(-2, -10, -3)
                .box(0, 0, 0, 2, 6, 2, stretch)
                .child()
                    .rotate(0, 0, 0.9F)
                    .around(-3.9F, -6, 0.001F)
                    .box(0, 0, 0, 2, 6, 2, stretch);

        rightHorn = ((PonyRenderer)head).child().tex(0, 52);
        rightHorn.tex(8, 52)
                .rotate(0.1F, 0, 0.8F)
                .offset(0, -10, -3)
                .box(0, 0, 0, 2, 6, 2, stretch)
                .child()
                    .rotate(0, 0, -0.9F)
                    .around(3.9F, -6, 0.001F)
                    .box(0, 0, 0, 2, 6, 2, stretch);

        headwear = new PonyRenderer(this, 32, 0)
                                     .offset(HEAD_CENTRE_X, HEAD_CENTRE_Y, HEAD_CENTRE_Z)
                                     .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                                     .box(-4, -4, -4, 8, 8, 8, stretch - 0.5F);

        snout = new PonySnout(this);
        snout.init(yOffset, stretch);
    }

    @Override
    protected void rotateArmHolding(Cuboid arm, float direction, float swingProgress, float ticks) {
        arm.pitch = -0.3707964F;
        arm.pitch += 0.4F + MathHelper.sin(ticks * 0.067F) / 10;
    }

    @Override
    protected void preInitLegs() {
        leftArm = new Cuboid(this, 0, 20);
        rightArm = new Cuboid(this, 0, 20);

        leftLeg = new Cuboid(this, 0, 20);
        rightLeg = new Cuboid(this, 0, 20);
    }

    @Override
    public boolean wingsAreOpen() {
        return isAttacking;
    }

    @Override
    protected float getLegRotationX() {
        return 3;
    }

    @Override
    protected float getArmRotationY() {
        return 14;
    }

    @Override
    protected int getArmLength() {
        return 30;
    }

    @Override
    public float getModelHeight() {
        return 3;
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        return MathHelper.sin(ticks) + WING_ROT_Z_SNEAK;
    }
}
