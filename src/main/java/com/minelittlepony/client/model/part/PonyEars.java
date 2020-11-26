package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.model.IPart;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.mson.api.model.PartBuilder;

import java.util.UUID;

public class PonyEars implements IPart, MsonModel {
    private ModelPart right;
    private ModelPart left;

    public PonyEars(ModelPart tree) {

    }

    @Override
    public void init(ModelContext context) {
        right = context.findByName("right");
        left = context.findByName("left");

        PartBuilder head = context.getContext();
        head.addChild("right_ear", right);
        head.addChild("left_ear", left);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
    }

    @Override
    public void setVisible(boolean visible) {
        right.visible = visible;
        left.visible = visible;
    }
}
