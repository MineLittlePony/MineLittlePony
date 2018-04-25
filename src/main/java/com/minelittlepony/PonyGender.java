package com.minelittlepony;

import com.minelittlepony.pony.data.ITriggerPixelMapped;

public enum PonyGender implements ITriggerPixelMapped<PonyGender> {
    MARE(0),
    STALLION(0xffffff);
    
    int triggerValue;
    
    PonyGender(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
