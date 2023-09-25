package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.mson.api.model.PartBuilder;

public class PonyEars implements IPart, MsonModel {
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
    public void setPartAngles(ModelAttributes attributes, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {
        right.resetTransform();

        limbSpeed = MathHelper.clamp(limbSpeed, 0, 1);

        float forwardFold = 0.14F * limbSpeed;
        float sidewaysFlop = 0.11F * limbSpeed;

        right.pitch = forwardFold;
        left.pitch = forwardFold;

        right.roll -= sidewaysFlop;
        left.roll  += sidewaysFlop;

        float floppyness = Math.abs(MathHelper.sin(animationProgress / 99F));
        if (floppyness > 0.99F) {
            boolean leftFlop = MathHelper.sin(animationProgress / 5F) > 0.5F;
            (leftFlop ? left : right).roll +=
                    0.01F * MathHelper.sin(animationProgress / 2F)
                  + 0.015F * MathHelper.cos(animationProgress / 3F);
        }
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
    }

    @Override
    public void setVisible(boolean visible, ModelAttributes attributes) {
        right.visible = visible && !attributes.metadata.race().isHuman();
        left.visible = visible && !attributes.metadata.race().isHuman();

        if (attributes.isHorsey) {
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
