package com.minelittlepony.pony.meta;

import com.minelittlepony.pony.ITriggerPixelMapped;

public enum TailLength implements ITriggerPixelMapped<TailLength> {

    STUB            (0x445842),
    QUARTER         (0xe49fd1),
    HALF            (0x764b53),
    THREE_QUARTERS  (0x7f6b8a),
    FULL            (0x000000);

    private int triggerValue;

    TailLength(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
