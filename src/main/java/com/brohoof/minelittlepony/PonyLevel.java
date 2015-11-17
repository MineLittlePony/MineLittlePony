package com.brohoof.minelittlepony;

public enum PonyLevel {
    PONIES,
    HUMANS,
    MIXED;

    private static final PonyLevel[] oldValues = { HUMANS, MIXED, PONIES };

    public static PonyLevel parse(int intValue) {
        if (intValue < 0)
            intValue = 0;
        if (intValue > 2)
            intValue = 2;
        // it's an old value
        return oldValues[intValue];
    }

    @Override
    public String toString() {
        return name().toString();
    }
}
