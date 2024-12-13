package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

import com.minelittlepony.mson.util.PartUtil;

public class PonyArmourStandModel extends ArmorStandEntityModel {
    public PonyArmourStandModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void copyTransforms(BipedEntityModel<ArmorStandEntityRenderState> model) {
        if (model instanceof PonyArmourStandModel) {
            super.copyTransforms(model);
        } else {
            PartUtil.copyAngles(head, model.head);
            PartUtil.copyAngles(rightArm, model.rightArm);
            PartUtil.copyAngles(leftArm, model.leftArm);
            PartUtil.copyAngles(rightLeg, model.rightLeg);
            PartUtil.copyAngles(leftLeg, model.leftLeg);
        }
    }

    @Override
    public void setAngles(ArmorStandEntityRenderState state) {
        super.setAngles(state);
    }
}
