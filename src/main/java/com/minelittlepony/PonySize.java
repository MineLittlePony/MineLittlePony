package com.minelittlepony;

public enum PonySize {
    NORMAL(0.4f, 1f),
    LARGE(0.5f, 0.8f),
    FOAL(0.25f, 0.8f),
    TALL(0.45f, 1f);

    private float shadowSize, scale;

    PonySize(float shadowSz, float scaleF) {
        shadowSize = shadowSz;
        scale = scaleF;
    }

    public float getShadowSize() {
        return shadowSize;
    }

    public float getScaleFactor() {
        return scale;
    }
}
