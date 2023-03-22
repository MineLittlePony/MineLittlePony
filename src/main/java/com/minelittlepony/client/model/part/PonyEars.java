package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

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
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
    }

    @Override
    public void setVisible(boolean visible, ModelAttributes attributes) {
        right.visible = visible && !attributes.metadata.getRace().isHuman();
        left.visible = visible && !attributes.metadata.getRace().isHuman();
    }
}
