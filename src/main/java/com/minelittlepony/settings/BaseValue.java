package com.minelittlepony.settings;

public class BaseValue<T> implements Value<T> {

    private T value;

    public BaseValue(T initial) {
        value = initial;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}
