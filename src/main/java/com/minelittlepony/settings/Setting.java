package com.minelittlepony.settings;


import java.lang.reflect.Field;

public interface Setting {

    String name();

    default boolean get() {
        try {
            Field field = getField();
            return field.getBoolean(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default void set(boolean value) {
        try {
            Field field = getField();
            field.setBoolean(null, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default Class<?> getEnclosingClass() {
        return getClass().getEnclosingClass();
    }

    default Field getField() {
        try {
            return getEnclosingClass().getField(name().toLowerCase());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
