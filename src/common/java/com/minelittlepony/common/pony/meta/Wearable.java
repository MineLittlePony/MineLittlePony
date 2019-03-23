package com.minelittlepony.common.pony.meta;

import com.minelittlepony.common.pony.ITriggerPixelMapped;

import java.util.ArrayList;
import java.util.List;

public enum Wearable implements ITriggerPixelMapped<Wearable> {
    NONE(0),
    MUFFIN(50),
    HAT(100),
    ANTLERS(150),
    SADDLE_BAGS(200),
    STETSON(250);

    private int triggerValue;

    Wearable(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }

    public static Wearable[] flags(boolean[] flags) {
        List<Wearable> wears = new ArrayList<Wearable>();
        Wearable[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (flags[i]) wears.add(values[i]);
        }
        return wears.toArray(new Wearable[wears.size()]);
    }
}
