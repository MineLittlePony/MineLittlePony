package com.minelittlepony.settings;

public interface Value<T> {

    T get();

    void set(T value);

    static <T> Value<T> of(T value) {
        return new BaseValue<>(value);
    }
}
