package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;

public enum Gender implements TriggerPixelType<Gender> {
    MARE(0),
    STALLION(0xffffff),
    ABOMONATION(0x888888);

    private int triggerValue;

    Gender(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getColorCode() {
        return triggerValue;
    }

    public boolean isMare() {
        return this == MARE;
    }

    public boolean isStallion() {
        return this == STALLION;
    }
}
