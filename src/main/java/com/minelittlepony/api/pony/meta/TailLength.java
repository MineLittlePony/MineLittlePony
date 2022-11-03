package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;

public enum TailLength implements TriggerPixelType<TailLength> {
    STUB            (0x425844),
    QUARTER         (0xd19fe4),
    HALF            (0x534b76),
    THREE_QUARTERS  (0x8a6b7f),
    FULL            (0x000000);

    private int triggerValue;

    TailLength(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getColorCode() {
        return triggerValue;
    }
}
