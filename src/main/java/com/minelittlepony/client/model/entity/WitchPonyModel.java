package com.minelittlepony.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.render.entity.WitchRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class WitchPonyModel extends EarthPonyModel<WitchRenderer.State> {
    public WitchPonyModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    public void setModelAngles(WitchRenderer.State entity) {
        super.setModelAngles(entity);

        if (entity.drinking) {
            float noseRot = Mth.sin(entity.ageInTicks);

            snout.rotate(noseRot * 4.5F * 0.02F, 0, noseRot * 2.5F * 0.02F);
        } else {
            snout.rotate(0, 0, 0);
        }

        if (!entity.getMainHandItemState().isEmpty()) {
            float rot = (float)(Math.tan(entity.ageInTicks / 7) + Math.sin(entity.ageInTicks / 3));
            if (rot > 1) rot = 1;
            if (rot < -1) rot = -1;

            float legDrinkingAngle = -1 * Mth.PI / 3F + rot;

            rightArm.xRot = legDrinkingAngle;
            rightArm.yRot = 0.1F;
            rightArm.x = 0.1F;

            if (rot > 0) {
                rot = 0;
            }

            head.xRot = -rot / 2;
        } else {
            rightArm.x = 0;
        }
    }

    @Override
    public void positionheldItem(WitchRenderer.State state, HumanoidArm arm, PoseStack matrices) {
        super.positionheldItem(state, arm, matrices);
        matrices.mulPose(Axis.XP.rotationDegrees(10));
    }
}
