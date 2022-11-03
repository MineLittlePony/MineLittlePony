package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

public class SpikeModel<T extends LivingEntity> extends BipedEntityModel<T> {

    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;

    public SpikeModel(ModelPart tree) {
        super(tree);
        tail = body.getChild("tail");
        tail2 = tail.getChild("tail2");
        tail3 = tail2.getChild("tail3");
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        swing *= 2;
        move *= 1.5F;
        child = false;

        head.pivotX = 0;
        head.pivotZ = 0;
        head.pivotY = 0;

        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);

        leftArm.pivotY++;
        rightArm.pivotY++;
        body.pitch += 0.15F;

        if ((entity instanceof StriderEntity strider && strider.isSaddled())) {
            leftArm.pitch = 3.15F;
            leftArm.yaw = 1;
            rightArm.pitch = 3.15F;
            rightArm.yaw = -1;

            head.pivotY += 4;
            head.pivotZ = -3;
            hat.pivotY += 4;
            hat.pivotZ = -3;

            leftLeg.pitch += 0.4F;
            rightLeg.pitch += 0.4F;
        } else {
            float flailAmount = 1 + (float)MathHelper.clamp(entity.getVelocity().y * 10, 0, 7);

            leftArm.roll -= 0.2F * flailAmount;
            rightArm.roll += 0.2F * flailAmount;

            leftArm.pivotZ += 2;
            leftArm.pitch -= 0.3F;

            rightArm.pivotZ += 2;
            rightArm.pitch -= 0.3F;

            if (entity instanceof StriderEntity strider && strider.isCold()) {
                float armMotion = (float)Math.sin(ticks / 10F) / 10F;

                leftArm.pitch = -1 - armMotion;
                rightArm.pitch = -1 + armMotion;

                leftArm.yaw = 0.8F;
                rightArm.yaw = -0.8F;

                leftArm.pivotZ -= 3;
                rightArm.pivotZ -= 3;
            }
        }

        tail.pitch = (float)Math.sin(move) / 3F - 0.5F;
        tail2.pitch = -tail.pitch / 2;
        tail3.pitch = tail2.pitch / 2;

        tail.yaw = (float)Math.sin(ticks / 20F) / 40 + (float)Math.sin(move / 20F) / 4;
        tail2.yaw = tail.yaw / 2;
        tail3.yaw = tail2.yaw / 2;

        for (var part : getHeadParts()) {
            part.pivotY += 7;
        }

        for (var part : getBodyParts()) {
            part.pivotY += 7;
        }
    }
}










