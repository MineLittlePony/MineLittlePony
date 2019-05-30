package com.minelittlepony.settings;

import com.minelittlepony.common.client.gui.IField.IChangeCallback;

import javax.annotation.Nonnull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A sensible config container that actually lets us programmatically index values by a key.
 *
 * Reflection because Mumfrey pls.
 *
 */
// Mumfrey pls.
// TODO: Reflection
public abstract class SensibleConfig {

    public abstract void save();

    public interface Setting extends IChangeCallback<Boolean> {
        String name();

        /**
         * Gets the config value associated with this entry.
         */
        default boolean get() {
            return config().getValue(this);
        }

        /**
         * Sets the config value associated with this entry.
         */
        default void set(boolean value) {
            config().setValue(this, value);
        }

        SensibleConfig config();

        @Override
        default Boolean perform(Boolean v) {
            set(v);
            return v;
        }
    }

    private Map<Setting, Boolean> entries = new HashMap<>();
    private Map<Setting, Field> fieldEntries = new HashMap<>();

    public boolean getValue(Setting key) {
        return entries.computeIfAbsent(key, this::reflectGetValue);
    }

    public boolean setValue(Setting key, boolean value) {
        entries.put(key, value);
        try {
            findField(getClass(), key).setBoolean(this, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return value;
    }

    @Nonnull
    protected Field findField(Class<?> type, Setting key) {
        return fieldEntries.computeIfAbsent(key, k -> recurseFindField(type, key));
    }

    private boolean reflectGetValue(Setting key) {
        try {
            return findField(getClass(), key).getBoolean(this);
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();

            return true;
        }
    }

    @Nonnull
    private Field recurseFindField(Class<?> type, Setting key) {
        try {
            Field f = type.getDeclaredField(key.name().toLowerCase());
            f.setAccessible(true);
            fieldEntries.put(key, f);

            return f;
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            Class<?> superType = type.getSuperclass();

            if (superType != null && superType != Object.class) {
                return recurseFindField(superType, key);
            }

            throw new RuntimeException(String.format("Config option %s was not defined", key), e);
        }
    }
}
