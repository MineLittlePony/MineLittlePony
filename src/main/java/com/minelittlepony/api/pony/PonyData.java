package com.minelittlepony.api.pony;

import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ComparisonChain;
import com.minelittlepony.api.pony.meta.*;

import java.util.*;
import java.util.function.Function;

/**
 * Metadata for a pony.
 */
public record PonyData (
        /**
         * Gets this pony's race.
         *
         * This is the actual race value. For the effective race, prefer going through {@link Pony#race}
         */
        Race race,
        /**
         * Gets the length of the pony's tail.
         */
        TailLength tailLength,
        /**
         * Gets the shape of the pony's tail.
         */
        TailShape tailShape,
        /**
         * Get the pony's gender (usually female).
         */
        Gender gender,
        /**
         * Gets the current pony size.
         */
        Size size,
        /**
         * Gets the magical glow colour for magic-casting races. Returns 0 otherwise.
         */
        int glowColor,
        /**
         * Returns an array of wearables that this pony is carrying.
         */
        Flags<Wearable> gear,
        /**
         * Indicates whether this pony data corresponds to one of the default/built-in skins
         * rather than a user-uploaded one.
         */
        boolean noSkin,
        /**
         * (Experimental) Priority.
         * Used to decide which skin to use when dual skin mode is active.
         * Provides an optional tie-breaker when the client has to decide between displaying
         * either the HDSkins texture or vanilla texture given both are otherwise acceptable.
         *
         * Any time both skins resolve to the same race (eg. on pony-level HUMANS, or if both are ponies)
         * the skin with the highest priority will be chosen.
         *
         * If both have the same priority, HD Skins' texture will always be used (old default).
         */
        int priority,
        /**
         * Gets the trigger pixel values as they appeared in the underlying image.
         */
        Map<String, TValue<?>> attributes
    ) implements Comparable<PonyData> {
    public static final int DEFAULT_MAGIC_COLOR = 0x4444aa;
    private static final Function<Race, PonyData> OF_RACE = Util.memoize(race -> new PonyData(race, TailLength.FULL, TailShape.STRAIGHT, Gender.MARE, SizePreset.NORMAL, DEFAULT_MAGIC_COLOR, true, 0, Wearable.EMPTY_FLAGS));
    public static final PonyData NULL = OF_RACE.apply(Race.HUMAN);

    public static PonyData emptyOf(Race race) {
        return OF_RACE.apply(race);
    }

    public PonyData(TriggerPixel.Mat image, boolean noSkin) {
        this(
            TriggerPixel.RACE.read(image),
            TriggerPixel.TAIL.read(image),
            TriggerPixel.TAIL_SHAPE.read(image),
            TriggerPixel.GENDER.read(image),
            TriggerPixel.SIZE.read(image),
            TriggerPixel.GLOW.read(image),
            noSkin,
            TriggerPixel.PRIORITY.read(image),
            TriggerPixel.WEARABLES.read(image)
        );
    }

    public PonyData(Race race, TailLength tailLength, TailShape tailShape, Gender gender, Size size, int glowColor, boolean noSkin, int priority, Flags<Wearable> wearables) {
        this(race, tailLength, tailShape, gender, size, glowColor, wearables, noSkin, priority, Util.make(new TreeMap<>(), map -> {
                map.put("race", race);
                map.put("tailLength", tailLength);
                map.put("tailShape", tailShape);
                map.put("gender", gender);
                map.put("size", size);
                map.put("magic", new TValue.Numeric(glowColor));
                map.put("priority", new TValue.Numeric(priority));
                map.put("gear", wearables);
            })
        );
    }

    @Override
    public int compareTo(@Nullable PonyData o) {
        return o == this ? 0  : o == null ? 1 : ComparisonChain.start()
                .compare(race(), o.race())
                .compare(tailLength(), o.tailLength())
                .compare(gender(), o.gender())
                .compare(size().ordinal(), o.size().ordinal())
                .compare(glowColor(), o.glowColor())
                .compare(0, gear().compareTo(o.gear()))
                .result();
    }
}
