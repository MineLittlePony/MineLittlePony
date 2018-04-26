package com.minelittlepony.pony.data;

public enum PonyGender implements ITriggerPixelMapped<PonyGender> {
    MARE(0),
    STALLION(0xffffff);
    
    private int triggerValue;
    
    PonyGender(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
