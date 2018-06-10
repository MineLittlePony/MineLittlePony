package com.minelittlepony.settings;


import java.lang.reflect.Field;

public interface Setting<Config> {

    String name();

    default boolean get(Config config) {
        try {
            Field field = getField(config.getClass());
            return field.getBoolean(config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default void set(Config config, boolean value) {
        try {
            Field field = getField(config.getClass());
            field.setBoolean(config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default Field getField(Class<?> owner) {
        try {
            return owner.getField(name().toLowerCase());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
