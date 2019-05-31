package com.minelittlepony.pony.meta;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.ITriggerPixelMapped;
import com.minelittlepony.settings.PonySettings;

public enum Size implements ITriggerPixelMapped<Size> {
    TALL    (0x764b53, 0.45f, 1.1F),
    BULKY   (0x5432ce, 0.5f,  1),
    LANKY   (0xce5432, 0.45F, 0.85F),
    NORMAL  (0x000000, 0.4f,  0.8F),
    YEARLING(0xffbe53, 0.4F,  0.6F),
    FOAL    (0x53beff, 0.25f, 0.6F);

    private int triggerValue;

    private float shadowSize;
    private float scale;

    Size(int pixel, float shadowSz, float scaleF) {
        triggerValue = pixel;
        shadowSize = shadowSz;
        scale = scaleF;
    }

    public float getShadowSize() {
        return shadowSize * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    public float getScaleFactor() {
        return scale * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }

    public Size getEffectiveSize() {
        return PonySettings.SIZES.get() ? this : Size.NORMAL;
    }
}
