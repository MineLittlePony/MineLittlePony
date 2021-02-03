package com.minelittlepony.api.pony;

/**
 * Interface for enums that can be parsed from an image trigger pixel value.
 */
public interface ITriggerPixelMapped<T extends Enum<T> & ITriggerPixelMapped<T>> {
    /**
     * Gets the pixel colour matching this enum value.
     */
    int getTriggerPixel();

    /**
     * Gets the enum value corresponding to the given enum type and pixel value.
     * If none are found, the first parameter is returned as the default.
     *
     * @param type Return type and default value.
     * @param pixelValue The pixel colour to search for.
     */
    @SuppressWarnings("unchecked")
    static <T extends Enum<T> & ITriggerPixelMapped<T>> T getByTriggerPixel(T type, int pixelValue) {
        for (T i : (T[])type.getClass().getEnumConstants()) {
            if (i.getTriggerPixel() == pixelValue) {
                return i;
            }
        }

        return type;
    }
}
