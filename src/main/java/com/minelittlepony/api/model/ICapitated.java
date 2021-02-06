package com.minelittlepony.api.model;

/**
 * Interface for models that have a head.
 */
public interface ICapitated<T> {
    /**
     * Gets the head of this capitated object.
     */
    T getHead();
}
