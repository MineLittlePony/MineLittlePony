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
    Race getRace();

    /**
     * Gets the length of the pony's tail.
     */
    TailLength getTailLength();

    /**
     * Gets the shape of the pony's tail.
     */
    TailShape getTailShape();

    /**
     * Get the pony's gender (usually female).
     */
    Gender getGender();

    /**
     * Gets the current pony size.
     */
    Size getSize();

    /**
     * Gets the magical glow colour for magic-casting races. Returns 0 otherwise.
     */
    int getGlowColor();

    /**
     * Returns an array of wearables that this pony is carrying.
     */
    Wearable[] getGear();

    /**
     * Checks it this pony is wearing the given accessory.
     */
    boolean isWearing(Wearable wearable);

    /**
     * Gets an interpolator for interpolating values.
     */
    Interpolator getInterpolator(UUID interpolatorId);

    /**
     * Gets the trigger pixel values as they appeared in the underlying image.
     */
    Map<String, TriggerPixelType<?>> getTriggerPixels();

    @Override
    default int compareTo(@Nullable IPonyData o) {
        return o == this ? 0  : o == null ? 1 : ComparisonChain.start()
                .compare(getRace(), o.getRace())
                .compare(getTailLength(), o.getTailLength())
                .compare(getGender(), o.getGender())
                .compare(getSize().ordinal(), o.getSize().ordinal())
                .compare(getGlowColor(), o.getGlowColor())
                .compare(0, Arrays.compare(getGear(), o.getGear()))
                .result();
    }
}
