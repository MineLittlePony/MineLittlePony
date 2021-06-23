package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

import com.minelittlepony.mson.util.PartUtil;

public class PonyArmourStandModel extends ArmorStandEntityModel {

    public PonyArmourStandModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(ArmorStandEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        leftArm.visible = true;
        rightArm.visible = true;

        PartUtil.copyAngles(leftArm, leftLeg);
        PartUtil.copyAngles(rightArm, rightLeg);

        leftLeg.pitch *= -1;
        rightLeg.pitch *= -1;
    }

    public void applyAnglesTo(BipedEntityModel<ArmorStandEntity> dest) {
        PartUtil.copyAngles(head, dest.head);
        PartUtil.copyAngles(hat, dest.hat);
        PartUtil.copyAngles(leftLeg, dest.leftLeg);
        PartUtil.copyAngles(rightLeg, dest.rightLeg);
        PartUtil.copyAngles(leftArm, dest.leftArm);
        PartUtil.copyAngles(rightArm, dest.rightArm);
    }
}
