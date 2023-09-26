package com.minelittlepony.api.model;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface SubModel {
    /**
     * Sets the model's various rotation angles.
     */
    default void setPartAngles(ModelAttributes attributes, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {

    }

    /**
     * Renders this model component.
     */
    void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, float red, float green, float blue, float alpha, ModelAttributes attributes);

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible, ModelAttributes attributes) {

    }
}
