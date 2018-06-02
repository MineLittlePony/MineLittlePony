package com.minelittlepony.pony.data;

public enum PonyAccessory implements ITriggerPixelMapped<PonyAccessory> {

    SADDLEBAGS(0x442300),
    NONE(0);

    private int triggerValue;

    PonyAccessory(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
