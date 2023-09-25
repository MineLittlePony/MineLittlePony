package com.minelittlepony.api.pony;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ComparisonChain;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * Metadata for a pony.
 */
public interface IPonyData extends Comparable<IPonyData> {
    /**
     * Gets this pony's race.
     *
     * This is the actual race value. For the effective race, prefer going through {@link IPony#race}
     */
    Race race();

    /**
     * Gets the length of the pony's tail.
     */
    TailLength tailLength();

    /**
     * Gets the shape of the pony's tail.
     */
    TailShape tailShape();

    /**
     * Get the pony's gender (usually female).
     */
    Gender gender();

    /**
     * Gets the current pony size.
     */
    Size size();

    /**
     * Gets the magical glow colour for magic-casting races. Returns 0 otherwise.
     */
    int glowColor();

    /**
     * Returns an array of wearables that this pony is carrying.
     */
    Wearable[] gear();

    /**
     * Checks it this pony is wearing the given accessory.
     */
    boolean isWearing(Wearable wearable);

    /**
     * Gets an interpolator for interpolating values.
     */
    default Interpolator getInterpolator(UUID interpolatorId) {
        return Interpolator.linear(interpolatorId);
    }

    /**
     * Gets the trigger pixel values as they appeared in the underlying image.
     */
    Map<String, TriggerPixelType<?>> attributes();

    @Override
    default int compareTo(@Nullable IPonyData o) {
        return o == this ? 0  : o == null ? 1 : ComparisonChain.start()
                .compare(race(), o.race())
                .compare(tailLength(), o.tailLength())
                .compare(gender(), o.gender())
                .compare(size().ordinal(), o.size().ordinal())
                .compare(glowColor(), o.glowColor())
                .compare(0, Arrays.compare(gear(), o.gear()))
                .result();
    }
}
