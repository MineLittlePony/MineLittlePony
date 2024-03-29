package com.minelittlepony.api.pony.meta;

public enum TailLength implements TValue<TailLength> {
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
    public int colorCode() {
        return triggerValue;
    }
}
