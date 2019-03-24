package com.minelittlepony.model;

import java.util.UUID;

public interface IPart {
    /**
     * Initialises all of the boxes in this modelpart.
     * @param yOffset
     * @param stretch
     */
    default void init(float yOffset, float stretch) {

    }

    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyMode.setRotationAndAngle} for an explanation of the various parameters.
     */
    default void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {

    }

    /**
     * Renders this model component.
     */
    void renderPart(float scale, UUID interpolatorId);

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible) {

    }
}
