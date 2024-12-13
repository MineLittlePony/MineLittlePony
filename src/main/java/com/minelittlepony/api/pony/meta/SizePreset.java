package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.config.PonyConfig;

/**
 * Represents the different model sizes that are possible.
 *
 * This is the client-side version.
 *
 * For spooky things at a distance, use {@link Size} instead.
 */
public enum SizePreset implements Size {
    TALL    (0x534b76, 0.45f, 1.1F,  1.15F),
    BULKY   (0xce3254, 0.5f,  1,     1.05F),
    LANKY   (0x3254ce, 0.45F, 0.85F, 0.9F),
    NORMAL  (0x000000, 0.4f,  0.8F,  0.8F),
    STOCKY  (0xb2e7dd, 0.45F, 0.8F,  0.8F),
    SQUAT   (0xa3d2c7, 0.4F,  0.7F,  0.67F),
    YEARLING(0x53beff, 0.4F,  0.6F,  0.65F),
    FOAL    (0xffbe53, 0.25f, 0.6F,  0.5F),
    UNSET   (0x000000, 1,     1,     1);

    private final int triggerValue;
    private final float shadowSize;
    private final float scale;
    private final float camera;

    SizePreset(int pixel, float shadowSz, float scaleF, float cameraF) {
        triggerValue = pixel;
        shadowSize = shadowSz;
        scale = scaleF;
        camera = cameraF;
    }

    @Override
    public int colorCode() {
        return triggerValue;
    }

    @Override
    public float shadowSize() {
        return shadowSize * PonyConfig.getInstance().getGlobalScaleFactor();
    }

    @Override
    public float scaleFactor() {
        return scale * PonyConfig.getInstance().getGlobalScaleFactor();
    }

    @Override
    public float eyeHeightFactor() {
        if (!PonyConfig.getInstance().fillycam.get()) {
            return 1;
        }
        return camera * PonyConfig.getInstance().getGlobalScaleFactor();
    }

    @Override
    public float eyeDistanceFactor() {
        return eyeHeightFactor();
    }
}
