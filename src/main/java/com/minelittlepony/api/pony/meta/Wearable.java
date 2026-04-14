package com.minelittlepony.api.pony.meta;

import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import com.minelittlepony.api.pony.Pony;
import com.mojang.serialization.Codec;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Wearable implements TValue<Wearable> {
    NONE              (0x00, null),
    CROWN             (0x16, Pony.id("textures/models/crown.png")),
    MUFFIN            (0x32, Pony.id("textures/models/muffin.png")),
    HAT               (0x64, Identifier.withDefaultNamespace("textures/entity/witch.png")),
    ANTLERS           (0x96, Pony.id("textures/models/antlers.png")),
    SADDLE_BAGS_LEFT  (0xC6, Pony.id("textures/models/saddlebags.png")),
    SADDLE_BAGS_RIGHT (0xC7, Pony.id("textures/models/saddlebags.png")),
    SADDLE_BAGS_BOTH  (0xC8, Pony.id("textures/models/saddlebags.png")),
    STETSON           (0xFA, Pony.id("textures/models/stetson.png")),
    MOUSE_EARS        (0xFB, Pony.id("textures/models/mouse_ears.png"));

    private int triggerValue;

    private final Identifier id;
    private final Identifier texture;

    public static final Map<Identifier, Wearable> REGISTRY = Arrays.stream(values()).collect(Collectors.toMap(Wearable::getId, Function.identity()));

    public static final Flags<Wearable> EMPTY_FLAGS = Flags.of(NONE);

    public static final Codecs<Wearable, EnumCodec<Wearable>> CODECS = TValue.codecs(Wearable::values);
    public static final Codec<Flags<Wearable>> FLAGS_CODEC = Flags.codec(NONE, CODECS.codec());

    Wearable(int pixel, Identifier texture) {
        triggerValue = pixel;
        id = Pony.id(name().toLowerCase(Locale.ROOT));
        this.texture = texture;
    }

    public Identifier getId() {
        return id;
    }

    public Identifier getDefaultTexture() {
        return texture;
    }

    @Override
    public int colorCode() {
        return triggerValue;
    }

    public boolean isSaddlebags() {
        return this == SADDLE_BAGS_BOTH || this == SADDLE_BAGS_LEFT || this == SADDLE_BAGS_RIGHT;
    }

    @Override
    public int getChannelAdjustedColorCode() {
        return triggerValue == 0 ? 0 : ARGB.color(255, triggerValue, triggerValue, triggerValue);
    }
}
