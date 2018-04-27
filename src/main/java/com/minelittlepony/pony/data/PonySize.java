package com.minelittlepony.pony.data;

public enum PonySize implements ITriggerPixelMapped<PonySize> {
    NORMAL(0, 0.4f, 1f),
    LARGE(0xce3254, 0.5f, 0.8f),
    FOAL(0xffbe53, 0.25f, 0.8f),
    TALL(0x534b76, 0.45f, 1f);

    private int triggerValue;

    private float shadowSize;
    private float scale;

    PonySize(int pixel, float shadowSz, float scaleF) {
        triggerValue = pixel;
        shadowSize = shadowSz;
        scale = scaleF;
    }

    public float getShadowSize() {
        return shadowSize;
    }

    public float getScaleFactor() {
        return scale;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
