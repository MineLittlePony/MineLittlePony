package com.minelittlepony.model.capabilities;

public interface IModelPegasus extends IModel {
    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen() {
        return isFlying() || isCrouching();
    }
}
