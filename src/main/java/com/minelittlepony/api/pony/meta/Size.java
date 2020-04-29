package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.ITriggerPixelMapped;
import com.minelittlepony.client.MineLittlePony;

public enum Size implements ITriggerPixelMapped<Size> {
    TALL    (0x534b76, 0.45f, 1.1F,  1.15F),
    BULKY   (0xce3254, 0.5f,  1,     1.05F),
    LANKY   (0x3254ce, 0.45F, 0.85F, 0.9F),
    NORMAL  (0x000000, 0.4f,  0.8F,  0.8F),
    YEARLING(0x53beff, 0.4F,  0.6F,  0.65F),
    FOAL    (0xffbe53, 0.25f, 0.6F,  0.5F),
    UNSET   (0x000000, 1,     1,     1);

    public static final Size[] REGISTRY = values();

    private int triggerValue;

    private float shadowSize;
    private float scale;
    private float camera;

    Size(int pixel, float shadowSz, float scaleF, float cameraF) {
        triggerValue = pixel;
        shadowSize = shadowSz;
        scale = scaleF;
        camera = cameraF;
    }

    public float getShadowSize() {
        return shadowSize * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    public float getScaleFactor() {
        return scale * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    public float getEyeHeightFactor() {
        if (!MineLittlePony.getInstance().getConfig().fillycam.get()) {
            return 1;
        }
        return camera * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    public float getEyeDistanceFactor() {
        if (!MineLittlePony.getInstance().getConfig().fillycam.get()) {
            return 1;
        }
        return camera * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }

    public Size getEffectiveSize() {
        Size sz = MineLittlePony.getInstance().getConfig().sizeOverride.get();

        if (sz != UNSET) {
            return sz;
        }

        if (this == UNSET || !MineLittlePony.getInstance().getConfig().sizes.get()) {
            return NORMAL;
        }

        return this;
    }
}
