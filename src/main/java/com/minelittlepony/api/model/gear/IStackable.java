package com.minelittlepony.api.model.gear;

/**
 * Interface for any gear that changes its position based on where it is in the hat stack.
 */
public interface IStackable {
    /**
     * The vertical height of this gear when present in a stack.
     *
     * Any gear rendered after this one will be shifted to sit on top of it.
     */
    float getStackingHeight();

}
