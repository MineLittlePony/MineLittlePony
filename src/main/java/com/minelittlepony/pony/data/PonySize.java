package com.minelittlepony.pony.data;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.transformation.*;

public enum PonySize implements ITriggerPixelMapped<PonySize> {
    NORMAL(0, 0.4f, 1f, new TransformNormal()),
    LARGE(0xce3254, 0.5f, 0.8f, new TransformLarge()),
    FOAL(0xffbe53, 0.25f, 0.8f, new TransformFoal()),
    TALL(0x534b76, 0.45f, 1f, new TransformTall());

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
