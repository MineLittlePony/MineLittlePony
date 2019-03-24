package com.minelittlepony.pony;

import com.minelittlepony.pony.meta.Gender;
import com.minelittlepony.pony.meta.Race;
import com.minelittlepony.pony.meta.Size;
import com.minelittlepony.pony.meta.TailLength;
import com.minelittlepony.pony.meta.Wearable;
import com.minelittlepony.util.animation.IInterpolator;

import java.util.UUID;

/**
 * Metadata for a pony.
 */
public interface IPonyData {
    /**
     * Gets this pony's race.
     */
    Race getRace();

    /**
     * Gets the length of the pony's tail.
     */
    TailLength getTail();

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
     * Returns true if and only if this metadata represents a pony that can cast magic.
     */
    boolean hasMagic();

    /**
     * Checks it this pony is wearing the given accessory.
     */
    boolean isWearing(Wearable wearable);

    /**
     * Gets an interpolator for interpolating values.
     */
    IInterpolator getInterpolator(UUID interpolatorId);
}
