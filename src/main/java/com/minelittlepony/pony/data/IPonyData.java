package com.minelittlepony.pony.data;

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
     * Returns true if and only if this metadata represents a pony that has bags.
     */
    boolean hasAccessory();

    /**
     * Returns true if and only if this metadata represents a pony that has bags.
     */
    boolean hasBags();
}
