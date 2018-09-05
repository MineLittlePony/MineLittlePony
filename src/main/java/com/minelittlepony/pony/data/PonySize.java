package com.minelittlepony.pony.data;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.transform.PonyTransformation;

public enum PonySize implements ITriggerPixelMapped<PonySize> {
    TALL(0x534b76, 0.45f, 1.1F, PonyTransformation.TALL),
    LARGE(0xce3254, 0.5f, 1, PonyTransformation.LARGE),
    NORMAL(0, 0.4f, 0.8F, PonyTransformation.NORMAL),
    YEARLING(0x53beff, 0.4F, 0.6F, PonyTransformation.YEARLING),
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
        return shadowSize * MineLittlePony.getConfig().getGlobalScaleFactor();
    }

    public float getScaleFactor() {
        return scale * MineLittlePony.getConfig().getGlobalScaleFactor();
    }

    public PonyTransformation getTranformation() {
        return transform;
    }

    @Override
    public int getTriggerPixel() {
        return triggerValue;
    }

    public PonySize getEffectiveSize() {
        return MineLittlePony.getConfig().getSizes() ? this : PonySize.NORMAL;
    }
}
