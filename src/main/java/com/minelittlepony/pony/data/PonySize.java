package com.minelittlepony.pony.data;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.transform.PonyTransformation;

public enum PonySize implements ITriggerPixelMapped<PonySize> {
    NORMAL(0, 0.4f, 1f, PonyTransformation.NORMAL),
    LARGE(0xce3254, 0.5f, 0.8f, PonyTransformation.LARGE),
    FOAL(0xffbe53, 0.25f, 0.8f, PonyTransformation.FOAL),
    TALL(0x534b76, 0.45f, 1f, PonyTransformation.TALL);

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
        if (MineLittlePony.getConfig().showscale) {
            return shadowSize * 0.9F;
        }
        return shadowSize;
    }

    public float getScaleFactor() {
        if (MineLittlePony.getConfig().showscale) {
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
