package com.minelittlepony.common.settings;

public enum PonyLevel {
    PONIES,
    HUMANS,
    BOTH;

    public static PonyLevel valueFor(float index) {
        PonyLevel[] values = values();
        if (index < 0) {
            index = 0;
        }
        return values[(int)Math.round(index) % values.length];
    }
}
