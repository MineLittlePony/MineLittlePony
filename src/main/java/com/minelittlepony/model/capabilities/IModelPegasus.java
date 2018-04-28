package com.minelittlepony.model.capabilities;

public interface IModelPegasus extends IModel {
    /**
     * Returns true if the wings are spread.
     */
    boolean wingsAreOpen();

    /**
     * Returns true if this model is being applied to a race that has wings.
     */
    boolean canFly();
}
