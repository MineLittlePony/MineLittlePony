package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.util.Arm;

public interface ModelWithHooves extends ModelWithArms {
    ModelPart getForeLeg(Arm side);

    ModelPart getHindLeg(Arm side);

    ArmPose getArmPoseForSide(Arm side);
}
