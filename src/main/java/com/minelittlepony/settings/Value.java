package com.minelittlepony.settings;

import com.minelittlepony.gui.IGuiCallback;

public interface Value<T> extends IGuiCallback<T> {

    T get();

    void set(T value);

    @Override
    default void perform(T in) {
        set(in);
    }

    static <T> Value<T> of(T value) {
        return new BaseValue<>(value);
    }
}
