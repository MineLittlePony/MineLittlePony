package com.minelittlepony.api.pony.meta;

import net.minecraft.util.StringIdentifiable;

import com.mojang.serialization.Codec;

public enum TailShape implements TValue<TailShape> {
    STRAIGHT(0),
    BUMPY   (0xfc539f),
    SWIRLY  (0x3eff22),
    SPIKY   (0x3308c7);

    public static final Codec<TailShape> CODEC = StringIdentifiable.createCodec(TailShape::values);

    private int triggerValue;

    TailShape(int pixel) {
        triggerValue = pixel;
    }

    @Override
    public int colorCode() {
        return triggerValue;
    }
}
