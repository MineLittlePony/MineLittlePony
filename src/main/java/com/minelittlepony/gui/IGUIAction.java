package com.minelittlepony.gui;

@FunctionalInterface
public interface IGUIAction<T> {
    T perform(T value);
}
