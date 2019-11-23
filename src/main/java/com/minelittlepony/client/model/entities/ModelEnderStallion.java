package com.minelittlepony.client.model.entities;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.MathHelper;

public class ModelEnderStallion extends ModelSkeletonPony<EndermanEntity> {

    public boolean isCarrying;
    public boolean isAttacking;

    public boolean isAlicorn;
    public boolean isBoss;

    private ModelPart leftHorn;
    private ModelPart rightHorn;

    public ModelEnderStallion() {
        super();
        attributes.visualHeight = 3;
    }

    @Override
    public void animateModel(EndermanEntity entity, float move, float swing, float ticks) {
        rightArmPose = isCarrying ? ArmPose.BLOCK : ArmPose.EMPTY;
        leftArmPose = rightArmPose;

        isUnicorn = true;
        isAlicorn = entity.getUuid().getLeastSignificantBits() % 3 == 0;
        isBoss = !isAlicorn && entity.getUuid().getLeastSignificantBits() % 90 == 0;

        leftHorn.visible = rightHorn.visible = !isBoss;
        horn.setVisible(!isBoss);

        leftHorn.pitch = 0.5F;
        rightHorn.pitch = 0.5F;
    }

    @Override
    public void setAngles(EndermanEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

        if (isAttacking) {
            head.pivotY -= 5;
        }
    }

    @Override
    public void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
        stack.push();
        stack.translate(0, -1.15F, 0);
        super.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
        stack.pop();
    }

    @Override
    public boolean canFly() {
        return isAlicorn;
    }

    @Override
    public void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        arm.pitch = -0.3707964F;
        arm.pitch += 0.4F + MathHelper.sin(ticks * 0.067F) / 10;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        tail.setVisible(false);

        snout.isHidden = true;
        leftHorn.visible = visible;
        rightHorn.visible = visible;

        leftSleeve.visible = false;
        rightSleeve.visible = false;

        leftPantLeg.visible = false;
        rightPantLeg.visible = false;
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
