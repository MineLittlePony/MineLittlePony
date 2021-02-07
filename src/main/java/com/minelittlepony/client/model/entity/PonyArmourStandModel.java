package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

import com.minelittlepony.mson.api.model.MsonPart;

public class PonyArmourStandModel extends ArmorStandEntityModel {

    public PonyArmourStandModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(ArmorStandEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        this.leftArm.visible = true;
        this.rightArm.visible = true;

        MsonPart.of(this.leftLeg).rotateTo(this.leftArm);
        MsonPart.of(this.rightLeg).rotateTo(this.rightArm);

        leftLeg.pitch *= -1;
        rightLeg.pitch *= -1;
    }

    public void applyAnglesTo(BipedEntityModel<ArmorStandEntity> dest) {
        MsonPart.of(dest.head).rotateTo(head);
        MsonPart.of(dest.hat).rotateTo(hat);
        MsonPart.of(dest.leftLeg).rotateTo(leftLeg);
        MsonPart.of(dest.rightLeg).rotateTo(rightLeg);
        MsonPart.of(dest.leftArm).rotateTo(leftArm);
        MsonPart.of(dest.rightArm).rotateTo(rightArm);
    }
}
