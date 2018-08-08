package com.minelittlepony.gui;

/**
 * Response actions for UI events.
 */
@FunctionalInterface
public interface IGuiCallback<T> {
    /**
     * Performs this action now.
     *
     * @param value    New Value of the field being changed
     * @return Adjusted value the field must take on
     */
    T perform(T value);
}
