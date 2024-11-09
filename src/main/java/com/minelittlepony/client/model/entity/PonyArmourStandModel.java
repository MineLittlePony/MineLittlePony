package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.entity.decoration.ArmorStandEntity;

import com.minelittlepony.mson.util.PartUtil;

public class PonyArmourStandModel extends ArmorStandEntityModel {
    public PonyArmourStandModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(ArmorStandEntityRenderState state) {
        super.setAngles(state);
        leftArm.visible = true;
        rightArm.visible = true;

        if (state.leftLegRotation.equals(ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION)) {
            PartUtil.copyAngles(leftArm, leftLeg);
            leftLeg.pitch *= -1;
        }

        if (state.rightLegRotation.equals(ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION)) {
            PartUtil.copyAngles(rightArm, rightLeg);
            rightLeg.pitch *= -1;
        }
    }
}
