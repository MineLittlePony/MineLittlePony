package com.minelittlepony;

import com.minelittlepony.pony.data.ITriggerPixelMapped;

public enum TailLengths implements ITriggerPixelMapped<TailLengths> {

    STUB(0x425844),
    QUARTER(0xd19fe4),
    HALF(0x534b76),
    THREE_QUARTERS(0x8a6b7f),
    FULL(0);
    
    private int triggerValue;
    
    TailLengths(int pixel) {
        triggerValue = pixel;
    }
    
    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
