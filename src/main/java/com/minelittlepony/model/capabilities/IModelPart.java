package com.minelittlepony.model.capabilities;

public interface IModelPart {
    /**
     * Initialises all of the boxes in this modelpart.
     * 
     * @param yOffset
     * @param stretch
     */
    void init(float yOffset, float stretch);

    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyMode.setRotationAndAngle} for an explanation of the various parameters.
     */
    void setRotationAndAngles(boolean rainboom, float move, float swing, float bodySwing, float ticks);

    /**
     * Renders this model component.
     */
    void renderPart(float scale);

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible) {

    }
}
