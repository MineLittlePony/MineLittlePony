package com.minelittlepony.common.client.gui;

/**
 * Response actions for UI events.
 */
@FunctionalInterface
public interface IGuiAction<T> {
    /**
     * Performs this action now.
     *
     * @param value    New Value of the field being changed
     * @return Adjusted value the field must take on
     */
    void perform(T sender);
}
