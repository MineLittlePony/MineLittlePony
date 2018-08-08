package com.minelittlepony.pony.data;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.transform.PonyTransformation;

public enum PonySize implements ITriggerPixelMapped<PonySize> {
    TALL(0x534b76, 0.45f, 1.1F, PonyTransformation.TALL),
    LARGE(0xce3254, 0.5f, 1, PonyTransformation.LARGE),
    NORMAL(0, 0.4f, 0.8F, PonyTransformation.NORMAL),
    FOAL(0xffbe53, 0.25f, 0.6F, PonyTransformation.FOAL);

    private int triggerValue;

    private float shadowSize;
    private float scale;

    private PonyTransformation transform;

    PonySize(int pixel, float shadowSz, float scaleF, PonyTransformation transformation) {
        triggerValue = pixel;
        shadowSize = shadowSz;
        scale = scaleF;
        transform = transformation;
    }

    public float getShadowSize() {
        if (MineLittlePony.getConfig().getShowscale().get()) {
            return shadowSize * 0.9F;
        }
        return shadowSize;
    }

    public float getScaleFactor() {
        if (MineLittlePony.getConfig().getShowscale().get()) {
            return scale * 0.9F;
        }
        return scale;
    }

    public PonyTransformation getTranformation() {
        return transform;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }
}
