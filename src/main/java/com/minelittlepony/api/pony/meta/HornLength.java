package com.minelittlepony.api.pony.meta;

public enum HornLength implements TValue<HornLength> {
    STUB            (0x6B2D5B),
    FOAL            (0xEFEDED),
    SHORT           (0xFFF2FC),
    FULL            (0x000000),
    LONG            (0xFEDEFF);

    public static final Codecs<HornLength, EnumCodec<HornLength>> CODECS = TValue.codecs(HornLength::values);

    private int triggerValue;

    HornLength(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int colorCode() {
        return triggerValue;
    }
}
