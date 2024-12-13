package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.render.entity.WitchRenderer;

public class WitchPonyModel extends EarthPonyModel<WitchRenderer.State> {
    public WitchPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void setModelAngles(WitchRenderer.State entity) {
        super.setModelAngles(entity);

        if (entity.drinking) {
            float noseRot = MathHelper.sin(entity.age);

            snout.rotate(noseRot * 4.5F * 0.02F, 0, noseRot * 2.5F * 0.02F);
        } else {
            snout.rotate(0, 0, 0);
        }

        if (!entity.getMainHandStack().isEmpty()) {
            float rot = (float)(Math.tan(entity.age / 7) + Math.sin(entity.age / 3));
            if (rot > 1) rot = 1;
            if (rot < -1) rot = -1;

            float legDrinkingAngle = -1 * MathHelper.PI / 3F + rot;

            rightArm.pitch = legDrinkingAngle;
            rightArm.yaw = 0.1F;
            rightArm.pivotX = 0.1F;

            if (rot > 0) {
                rot = 0;
            }

            head.pitch = -rot / 2;
        } else {
            rightArm.pivotX = 0;
        }
    }

    @Override
    public void positionheldItem(WitchRenderer.State state, Arm arm, MatrixStack matrices) {
        super.positionheldItem(state, arm, matrices);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10));
    }
}
