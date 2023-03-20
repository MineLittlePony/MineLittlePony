package com.minelittlepony.api.model;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface IPart extends PonyModelConstants {
    /**
     * Sets the model's various rotation angles.
     * <p>
     * See {@link AbstractPonyMode.setRotationAndAngle} for an explanation of the various parameters.
     */
    default void setRotationAndAngles(ModelAttributes attributes, float move, float swing, float bodySwing, float ticks) {

    }

    /**
     * Renders this model component.
     */
    void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes);

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible) {

    }
}
