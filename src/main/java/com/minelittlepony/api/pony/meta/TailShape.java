package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;

public enum TailShape implements TriggerPixelType<TailShape> {
    STRAIGHT(0),
    BUMPY   (0xfc539f),
    SWIRLY  (0x3eff22),
    SPIKY   (0x3308c7);

    private int triggerValue;

    TailShape(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getColorCode() {
        return triggerValue;
    }
}
