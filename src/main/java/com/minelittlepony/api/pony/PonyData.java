package com.minelittlepony.api.pony;

import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ComparisonChain;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.common.util.animation.Interpolator;

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
         * Gets the trigger pixel values as they appeared in the underlying image.
         */
        Map<String, TriggerPixelType<?>> attributes
    ) implements Comparable<PonyData> {
    private static final Function<Race, PonyData> OF_RACE = Util.memoize(race -> {
        return new PonyData(race, TailLength.FULL, TailShape.STRAIGHT, Gender.MARE, Sizes.NORMAL, 0x4444aa, Wearable.EMPTY_FLAGS, true, Util.make(new TreeMap<>(), attributes -> {
            attributes.put("race", race);
            attributes.put("tailLength", TailLength.FULL);
            attributes.put("tailShape", TailShape.STRAIGHT);
            attributes.put("gender", Gender.MARE);
            attributes.put("size", Sizes.NORMAL);
            attributes.put("magic", TriggerPixelType.of(0x4444aa));
            attributes.put("gear", TriggerPixelType.of(0));
        }));
    });
    public static final PonyData NULL = OF_RACE.apply(Race.HUMAN);

    public static PonyData emptyOf(Race race) {
        return OF_RACE.apply(race);
    }


    public PonyData(Race race, TailLength tailLength, TailShape tailShape, Gender gender, Size size, int glowColor, boolean noSkin, Flags<Wearable> wearables) {
        this(race, tailLength, tailShape, gender, size, glowColor, wearables, noSkin, Util.make(new TreeMap<>(), map -> {
            map.put("race", race);
            map.put("tailLength", tailLength);
            map.put("tailShape", tailShape);
            map.put("gender", gender);
            map.put("size", size);
            map.put("magic", TriggerPixelType.of(glowColor));
            map.put("gear", TriggerPixelType.of(wearables.colorCode()));
        }));
    }
    public PonyData(Race race, TailLength tailLength, TailShape tailShape, Gender gender, Size size, int glowColor, TriggerPixelType.Multiple<Wearable> wearables, boolean noSkin) {
        this(race, tailLength, tailShape, gender, size, glowColor,
                Flags.of(Wearable.class, wearables.colorCode(), wearables.value()),
                noSkin, Util.make(new TreeMap<>(), map -> {
                    map.put("race", race);
                    map.put("tailLength", tailLength);
                    map.put("tailShape", tailShape);
                    map.put("gender", gender);
                    map.put("size", size);
                    map.put("magic", TriggerPixelType.of(glowColor));
                    map.put("gear", wearables);
                })
        );
    }

    /**
     * Checks it this pony is wearing the given accessory.
     */
    public boolean isWearing(Wearable wearable) {
        return gear().includes(wearable);
    }

    /**
     * Gets an interpolator for interpolating values.
     */
    public Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
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
