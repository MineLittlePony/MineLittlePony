package com.minelittlepony.api.pony.meta;

public enum HornLength implements TValue<HornLength> {
    STUB            (0x6B2D5B, 0, 0.25F),
    FOAL            (0xEFEDED, 1, 0.5F),
    SHORT           (0xFFF2FC, 2, 0.75F),
    FULL            (0x000000, 3, 1F),
    LONG            (0xFEDEFF, 4, 1.25F);

    public static final Codecs<HornLength, EnumCodec<HornLength>> CODECS = TValue.codecs(HornLength::values);

    private final int triggerValue;
    private final int order;
    private final float glowScale;

    HornLength(int pixel, int value, float glowSize) {
        triggerValue = pixel;
        order = value;
        glowScale = glowSize;
    }

    @Override
    public int colorCode() {
        return triggerValue;
    }

    public int getValue() {
        return this.order;
    }

    public float getGlowSize() {
        return this.glowScale;
    }
}
