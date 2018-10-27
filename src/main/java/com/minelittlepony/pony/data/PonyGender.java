package com.minelittlepony.pony.data;

public enum PonyGender implements ITriggerPixelMapped<PonyGender> {
    MARE(0),
    STALLION(0xffffff),
    ABOMONATION(0x888888);

    private int triggerValue;

    PonyGender(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }

    public boolean isMare() {
        return this == MARE;
    }

    public boolean isStallion() {
        return this == STALLION;
    }
}
