package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Rotations;
import net.minecraft.util.Mth;

import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.entity.PonyStandRenderer;

public class PonyArmorStandEntityArmorModel extends PonyArmourModel<PonyStandRenderer.PonyState> {
    public PonyArmorStandEntityArmorModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setModelAngles(PonyStandRenderer.PonyState state) {
        super.setModelAngles(state);
        setRotation(head, state.angles.headPose);
        setRotation(body, state.angles.bodyPose);
        setRotation(leftArm, state.angles.leftArmPose);
        setRotation(rightArm, state.angles.rightArmPose);
        setRotation(leftLeg, state.angles.leftLegPose);
        setRotation(rightLeg, state.angles.rightLegPose);
    }

    static void setRotation(ModelPart part, Rotations rotation) {
        part.setRotation(rotation.x() * Mth.DEG_TO_RAD, rotation.y() * Mth.DEG_TO_RAD, rotation.z() * Mth.DEG_TO_RAD);
    }
}
