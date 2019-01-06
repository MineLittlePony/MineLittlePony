package com.minelittlepony.pony.data;

import com.minelittlepony.model.anim.IInterpolator;

import java.util.UUID;

import net.minecraft.client.resources.data.IMetadataSection;

/**
 * Metadata for a pony.
 */
public interface IPonyData extends IMetadataSection {
    /**
     * Gets this pony's race.
     */
    PonyRace getRace();

    /**
     * Gets the length of the pony's tail.
     */
    TailLengths getTail();

    /**
     * Get the pony's gender (usually female).
     */
    PonyGender getGender();

    /**
     * Gets the current pony size.
     */
    PonySize getSize();

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
    boolean isWearing(PonyWearable wearable);

    /**
     * Gets an interpolator for interpolating values.
     */
    IInterpolator getInterpolator(UUID interpolatorId);
}
