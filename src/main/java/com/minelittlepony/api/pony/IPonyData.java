package com.minelittlepony.api.pony;

import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.TailLength;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.util.animation.Interpolator;

import java.util.Map;
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
     * Returns true if and only if this metadata represents a pony that has a horn.
     */
    boolean hasHorn();

    /**
     * Returns true if and only if this metadata represents a pony that can cast magic.
     */
    default boolean hasMagic() {
        return hasHorn() && getGlowColor() != 0;
    }

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
}
