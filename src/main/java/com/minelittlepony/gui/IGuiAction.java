package com.minelittlepony.gui;

/**
 * Response actions for UI events.
 */
@FunctionalInterface
public interface IGuiAction<T> {
    /**
     * Performs this action now.
     *
     * @param sender    the element handling this event
     */
    void perform(T sender);

}
