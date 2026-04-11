package com.minelittlepony.api.pony.meta;

public enum Gender implements TValue<Gender> {
    MARE(0),
    STALLION(0xffffff),
    ABOMONATION(0x888888);

    public static final Codecs<Gender, EnumCodec<Gender>> CODECS = TValue.codecs(Gender::values);

    private int triggerValue;

    Gender(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int colorCode() {
        return triggerValue;
    }

    public boolean isMare() {
        return this == MARE;
    }

    public boolean isStallion() {
        return this == STALLION;
    }
}
