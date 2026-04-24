package com.minelittlepony.client.model.part;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.mson.api.model.PartBuilder;

public class PonyEars implements SubModel<PonyRenderState>, MsonModel {
    private final ModelPart right;
    private final ModelPart left;

    public PonyEars(ModelPart tree) {
        right = tree.getChild("right");
        left = tree.getChild("left");
    }

    @Override
    public void init(ModelView context) {
        PartBuilder head = context.getThis();
        head.addChild("right_ear_" + hashCode(), right);
        head.addChild("left_ear_" + hashCode(), left);
    }

    @Override
    public void setAngles(PonyModel<PonyRenderState> model, PonyRenderState state) {
        left.resetPose();
        right.resetPose();
        right.visible = !state.race.isHuman();
        left.visible = !state.race.isHuman();

        if (state.attributes.isHorsey) {
            left.x = -1;
            right.x = 1;
            left.y = right.y = 1;
            left.z = right.z = 1.5F;
        }

        float limbSpeed = Mth.clamp(state.walkAnimationSpeed, 0, 1);

        float forwardFold = 0.14F * limbSpeed;
        float sidewaysFlop = 0.11F * limbSpeed;

        right.xRot += forwardFold;
        left.xRot += forwardFold;

        right.zRot -= sidewaysFlop;
        left.zRot  += sidewaysFlop;

        float floppyness = Math.abs(Mth.sin(state.ageInTicks / 99F));
        if (floppyness > 0.99F) {
            boolean leftFlop = Mth.sin(state.ageInTicks / 5F) > 0.5F;
            (leftFlop ? left : right).zRot +=
                    0.01F * Mth.sin(state.ageInTicks / 2F)
                  + 0.015F * Mth.cos(state.ageInTicks / 3F);
        }
    }
}
