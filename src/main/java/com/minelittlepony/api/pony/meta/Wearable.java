package com.minelittlepony.api.pony.meta;

import net.minecraft.util.Identifier;

import com.minelittlepony.common.util.Color;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Wearable implements TriggerPixelType<Wearable> {
    NONE              (0x00, null),
    CROWN             (0x16, new Identifier("minelittlepony", "textures/models/crown.png")),
    MUFFIN            (0x32, new Identifier("minelittlepony", "textures/models/muffin.png")),
    HAT               (0x64, new Identifier("textures/entity/witch.png")),
    ANTLERS           (0x96, new Identifier("minelittlepony", "textures/models/antlers.png")),
    SADDLE_BAGS_LEFT  (0xC6, new Identifier("minelittlepony", "textures/models/saddlebags.png")),
    SADDLE_BAGS_RIGHT (0xC7, new Identifier("minelittlepony", "textures/models/saddlebags.png")),
    SADDLE_BAGS_BOTH  (0xC8, new Identifier("minelittlepony", "textures/models/saddlebags.png")),
    STETSON           (0xFA, new Identifier("minelittlepony", "textures/models/stetson.png"));

    private int triggerValue;

    private final Identifier id;

    private final Identifier texture;

    public static final List<Wearable> VALUES = Arrays.stream(values()).toList();
    public static final Map<Identifier, Wearable> REGISTRY = VALUES.stream().collect(Collectors.toMap(Wearable::getId, Function.identity()));

    public static final Flags<Wearable> EMPTY_FLAGS = Flags.of(Wearable.class);

    Wearable(int pixel, Identifier texture) {
        triggerValue = pixel;
        id = new Identifier("minelittlepony", name().toLowerCase(Locale.ROOT));
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
        return triggerValue == 0 ? 0 : Color.argbToHex(255, triggerValue, triggerValue, triggerValue);
    }
}
