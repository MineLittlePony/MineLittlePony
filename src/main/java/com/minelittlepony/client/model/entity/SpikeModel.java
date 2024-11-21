package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;

import com.minelittlepony.client.render.entity.StriderRenderer;

public class SpikeModel extends BipedEntityModel<StriderRenderer.State> {

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
    public void setAngles(StriderRenderer.State entity) {
        entity.limbFrequency *= 2;
        entity.limbAmplitudeMultiplier *= 1.5F;
        entity.baby = false;

        head.pivotX = 0;
        head.pivotZ = 0;
        head.pivotY = 0;

        super.setAngles(entity);

        leftArm.pivotY++;
        rightArm.pivotY++;
        body.pitch += 0.15F;

        if (entity.saddled) {
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
            leftArm.roll -= 0.2F * entity.flailAmount;
            rightArm.roll += 0.2F * entity.flailAmount;

            leftArm.pivotZ += 2;
            leftArm.pitch -= 0.3F;

            rightArm.pivotZ += 2;
            rightArm.pitch -= 0.3F;

            if (entity.cold) {
                float armMotion = (float)Math.sin(entity.age / 10F) / 10F;

                leftArm.pitch = -1 - armMotion;
                rightArm.pitch = -1 + armMotion;

                leftArm.yaw = 0.8F;
                rightArm.yaw = -0.8F;

                leftArm.pivotZ -= 3;
                rightArm.pivotZ -= 3;
            }
        }

        tail.pitch = (float)Math.sin(entity.limbFrequency) / 3F - 0.5F;
        tail2.pitch = -tail.pitch / 2;
        tail3.pitch = tail2.pitch / 2;

        tail.yaw = (float)Math.sin(entity.age / 20F) / 40 + (float)Math.sin(entity.limbFrequency / 20F) / 4;
        tail2.yaw = tail.yaw / 2;
        tail3.yaw = tail2.yaw / 2;

        getRootPart().pivotY += 7;
    }
}










