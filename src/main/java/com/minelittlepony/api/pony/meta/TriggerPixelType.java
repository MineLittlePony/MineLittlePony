package com.minelittlepony.api.pony.meta;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for enums that can be parsed from an image trigger pixel value.
 */
public interface TriggerPixelType<T extends TriggerPixelType<T>> {
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
    default String name() {
        return "[Numeric " + getHexValue() + "]";
    }

    default String getHexValue() {
        return toHex(colorCode());
    }

    /**
     * Returns a list of possible values this trigger pixel can accept.
     */
    @SuppressWarnings("unchecked")
    default List<TriggerPixelType<T>> getOptions() {
        if (this instanceof Enum) {
            // cast is required because gradle's compiler is more strict
            return Arrays.asList(getClass().getEnumConstants());
        }
        return List.of();
    }

    default boolean matches(Object o) {
        return equals(o);
    }

    /**
     * Gets the enum value corresponding to the given enum type and pixel value.
     * If none are found, the first parameter is returned as the default.
     *
     * @param type Return type and default value.
     * @param pixelValue The pixel colour to search for.
     */
    @SuppressWarnings("unchecked")
    static <T extends TriggerPixelType<T>> T getByTriggerPixel(T type, int pixelValue) {
        return (T)type.getOptions().stream()
                .filter(i -> i.colorCode() == pixelValue)
                .findFirst()
                .orElse(type);
    }

    static <T extends TriggerPixelType<T>> TriggerPixelType<T> of(int color) {
        return () -> color;
    }

    static String toHex(int color) {
        String v = Integer.toHexString(color).toUpperCase();
        while (v.length() < 6) {
            v = "0" + v;
        }
        return "#" + v;
    }

    public record Multiple<T extends Enum<T> & TriggerPixelType<T>> (
            int colorCode,
            T def,
            boolean[] value
        ) implements TriggerPixelType<T> {
        @Override
        public String name() {
            return "[Flags " + Arrays.toString(value) + "]";
        }

        @Override
        public List<TriggerPixelType<T>> getOptions() {
            return def.getOptions();
        }

        @Override
        public boolean matches(Object o) {
            return o.getClass() == def.getClass() && value()[((Enum<?>)o).ordinal()];
        }
    }
}
