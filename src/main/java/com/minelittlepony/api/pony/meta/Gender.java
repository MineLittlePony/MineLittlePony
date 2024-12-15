package com.minelittlepony.api.pony.meta;

import net.minecraft.util.StringIdentifiable;

import com.mojang.serialization.Codec;

public enum Gender implements TValue<Gender> {
    MARE(0),
    STALLION(0xffffff),
    ABOMONATION(0x888888);

    public static final Codec<Gender> CODEC = StringIdentifiable.createCodec(Gender::values);

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
