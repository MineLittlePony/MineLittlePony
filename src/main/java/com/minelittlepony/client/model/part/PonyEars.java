package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

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
    public void setPartAngles(PonyRenderState state, float bodySwing) {
        right.resetTransform();
        left.resetTransform();

        float limbSpeed = MathHelper.clamp(state.limbAmplitudeMultiplier, 0, 1);

        float forwardFold = 0.14F * limbSpeed;
        float sidewaysFlop = 0.11F * limbSpeed;

        right.pitch += forwardFold;
        left.pitch += forwardFold;

        right.roll -= sidewaysFlop;
        left.roll  += sidewaysFlop;

        float floppyness = Math.abs(MathHelper.sin(state.age / 99F));
        if (floppyness > 0.99F) {
            boolean leftFlop = MathHelper.sin(state.age / 5F) > 0.5F;
            (leftFlop ? left : right).roll +=
                    0.01F * MathHelper.sin(state.age / 2F)
                  + 0.015F * MathHelper.cos(state.age / 3F);
        }
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
    }

    @Override
    public void setVisible(boolean visible, PonyRenderState state) {
        right.visible = !state.race.isHuman();
        left.visible = !state.race.isHuman();

        if (state.attributes.isHorsey) {
            left.pivotX = -1;
            right.pivotX = 1;
            left.pivotY = right.pivotY = 1;
            left.pivotZ = right.pivotZ = 1.5F;
        } else {
            left.resetTransform();
            right.resetTransform();
        }
    }
}
