package com.minelittlepony.api.pony.meta;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for enums that can be parsed from an image trigger pixel value.
 */
public interface TValue<T> {
    /**
     * Gets the pixel colour matching this enum value.
     */
    int colorCode();

    /**
     * Gets the pixel colour matching this enum value, adjusted to fill all three channels.
     */
    default int getChannelAdjustedColorCode() {
        return colorCode();
    }

    /**
     * Gets a string representation of this value.
     */
    String name();

    default String getHexValue() {
        return toHex(colorCode());
    }

    /**
     * Returns a list of possible values this trigger pixel can accept.
     */
    @SuppressWarnings("unchecked")
    default List<TValue<T>> getOptions() {
        if (this instanceof Enum) {
            // cast is required because gradle's compiler is more strict
            return Arrays.asList(getClass().getEnumConstants());
        }
        return List.of();
    }

    default boolean matches(TValue<?> o) {
        return o != null && colorCode() == o.colorCode();
    }

    static String toHex(int color) {
        String v = Integer.toHexString(color).toUpperCase();
        while (v.length() < 6) {
            v = "0" + v;
        }
        return "#" + v;
    }

    public record Numeric(int colorCode) implements TValue<Integer> {
        @Override
        public String name() {
            return "[Numeric " + getHexValue() + "]";
        }

        @Override
        public List<TValue<Integer>> getOptions() {
            return List.of();
        }
    }
}
