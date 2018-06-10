package com.minelittlepony.pony.data;

public enum PonyLevel {
    PONIES,
    HUMANS,
    BOTH;

    public static PonyLevel valueFor(int index) {
        PonyLevel[] values = values();
        if (index < 0) {
            index = 0;
        }
        return values[Math.round(index) % values.length];
    }
}
