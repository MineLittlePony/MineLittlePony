package com.minelittlepony.pony.data;

import java.util.ArrayList;
import java.util.List;

public enum PonyWearable implements ITriggerPixelMapped<PonyWearable> {
    NONE(0),
    SADDLE_BAGS(255),
    HAT(100);

    private int triggerValue;

    PonyWearable(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }

    public static PonyWearable[] flags(boolean[] flags) {
        List<PonyWearable> wears = new ArrayList<>();
        PonyWearable[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (flags[i]) {
                wears.add(values[i]);
            }
        }
        return wears.toArray(new PonyWearable[wears.size()]);
    }
}
