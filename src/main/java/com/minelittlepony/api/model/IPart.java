package com.minelittlepony.api.model;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.UUID;

public interface IPart {
    /**
     * Sets the model's various rotation angles.
     */
    default void setPartAngles(ModelAttributes attributes, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {

    }

    @Deprecated
    default void setRotationAndAngles(boolean goingFast, UUID interpolatorId, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {
        Compat.attributes.isGoingFast = goingFast;
        Compat.attributes.interpolatorId = interpolatorId;
        setPartAngles(Compat.attributes, limbAngle, limbSpeed, bodySwing, animationProgress);
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

    @Deprecated
    default void setVisible(boolean visible) {
        setVisible(visible, Compat.attributes);
    }

    @Deprecated
    class Compat {
        public static ModelAttributes attributes = new ModelAttributes();
    }
}
