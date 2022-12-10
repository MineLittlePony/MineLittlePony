package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;
import com.minelittlepony.common.util.Color;

import java.util.ArrayList;
import java.util.List;

public enum Wearable implements TriggerPixelType<Wearable> {
    NONE              (0x00),
    CROWN             (0x16),
    MUFFIN            (0x32),
    HAT               (0x64),
    ANTLERS           (0x96),
    SADDLE_BAGS_LEFT  (0xC6),
    SADDLE_BAGS_RIGHT (0xC7),
    SADDLE_BAGS_BOTH  (0xC8),
    STETSON           (0xFA);

    private int triggerValue;

    Wearable(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int getColorCode() {
        return triggerValue;
    }

    public boolean isSaddlebags() {
        return this == SADDLE_BAGS_BOTH || this == SADDLE_BAGS_LEFT || this == SADDLE_BAGS_RIGHT;
    }

    @Override
    public int getChannelAdjustedColorCode() {
        return triggerValue == 0 ? 0 : Color.argbToHex(255, triggerValue, triggerValue, triggerValue);
    }

    public static boolean[] flags(Wearable[] wears) {
        boolean[] flags = new boolean[values().length];
        for (int i = 0; i < wears.length; i++) {
            flags[wears[i].ordinal()] = true;
        }
        return flags;
    }

    public static Wearable[] flags(boolean[] flags) {
        List<Wearable> wears = new ArrayList<>();
        Wearable[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (flags[i]) wears.add(values[i]);
        }
        return wears.toArray(new Wearable[0]);
    }
}
