package com.minelittlepony.api.pony.meta;

import com.minelittlepony.client.MineLittlePony;

/**
 * Represents the different model sizes that are possible.
 *
 * This is the client-side version.
 *
 * For spooky things at a distance, use {@link Size} instead.
 */
public enum Sizes implements Size {
    TALL    (0x534b76, 0.45f, 1.1F,  1.15F),
    BULKY   (0xce3254, 0.5f,  1,     1.05F),
    LANKY   (0x3254ce, 0.45F, 0.85F, 0.9F),
    NORMAL  (0x000000, 0.4f,  0.8F,  0.8F),
    YEARLING(0x53beff, 0.4F,  0.6F,  0.65F),
    FOAL    (0xffbe53, 0.25f, 0.6F,  0.5F),
    UNSET   (0x000000, 1,     1,     1);

    public static final Sizes[] REGISTRY = values();

    private int triggerValue;

    private float shadowSize;
    private float scale;
    private float camera;

    Sizes(int pixel, float shadowSz, float scaleF, float cameraF) {
        triggerValue = pixel;
        shadowSize = shadowSz;
        scale = scaleF;
        camera = cameraF;
    }

    @Override
    public float getShadowSize() {
        return shadowSize * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    @Override
    public float getScaleFactor() {
        return scale * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    @Override
    public float getEyeHeightFactor() {
        if (!MineLittlePony.getInstance().getConfig().fillycam.get()) {
            return 1;
        }
        return camera * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    @Override
    public float getEyeDistanceFactor() {
        if (!MineLittlePony.getInstance().getConfig().fillycam.get()) {
            return 1;
        }
        return camera * MineLittlePony.getInstance().getConfig().getGlobalScaleFactor();
    }

    @Override
    public int getColorCode() {
        return triggerValue;
    }

    public static Sizes of(Size size) {
        if (size instanceof Sizes) {
            return (Sizes)size;
        }
        int i = size.ordinal();
        if (i < 0 || i >= REGISTRY.length) {
            return UNSET;
        }
        return REGISTRY[i];
    }
}
