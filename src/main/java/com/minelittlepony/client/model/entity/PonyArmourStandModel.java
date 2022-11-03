package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

import com.minelittlepony.mson.util.PartUtil;

public class PonyArmourStandModel extends ArmorStandEntityModel {
    private static final EulerAngle DEFAULT_LEFT_LEG_ROTATION = new EulerAngle(-1, 0, -1);
    private static final EulerAngle DEFAULT_RIGHT_LEG_ROTATION = new EulerAngle(1, 0, 1);

    public PonyArmourStandModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(ArmorStandEntity entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        leftArm.visible = true;
        rightArm.visible = true;

        if (entity.getLeftLegRotation().equals(DEFAULT_LEFT_LEG_ROTATION)) {
            PartUtil.copyAngles(leftArm, leftLeg);
            leftLeg.pitch *= -1;
        }

        if (entity.getRightLegRotation().equals(DEFAULT_RIGHT_LEG_ROTATION)) {
            PartUtil.copyAngles(rightArm, rightLeg);
            rightLeg.pitch *= -1;
        }
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
