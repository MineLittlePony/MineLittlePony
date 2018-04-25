package com.minelittlepony.pony.data;

public interface ITriggerPixelMapped<T extends Enum<T> & ITriggerPixelMapped<T>> {
    
    int getTriggerPixel();
    
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & ITriggerPixelMapped<T>> T getByTriggerPixel(T type, int pixelValue) {
        for (T i : (T[])type.getClass().getEnumConstants()) {
            if (i.getTriggerPixel() == pixelValue) return i;
        }
        return type;
    }
}
