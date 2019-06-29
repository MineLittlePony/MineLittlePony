package com.minelittlepony.pony.meta;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.ITriggerPixelMapped;
import com.minelittlepony.settings.PonyLevel;

import javax.annotation.Nonnull;

public enum Race implements ITriggerPixelMapped<Race> {

    HUMAN       (0x000000, false, false),
    EARTH       (0x31b1f9, false, false),
    PEGASUS     (0xf0ca88, true,  false),
    UNICORN     (0xe49fd1, false, true),
    ALICORN     (0xfcf9fe, true,  true),
    CHANGELING  (0x292b28, true,  true),
    ZEBRA       (0xcfccd0, false, false),
    CHANGEDLING (0x5aedca, CHANGELING),
    GRIFFIN     (0x4591ae, PEGASUS),
    HIPPOGRIFF  (0xacddd6, PEGASUS),
    KIRIN       (0xaf88fa, UNICORN),
    BATPONY     (0xeeeeee, true,  false),
    SEAPONY     (0xdd5536, false, true);

    private boolean wings;
    private boolean horn;

    private int triggerPixel;

    private final Race original;

    Race(int triggerPixel, boolean wings, boolean horn) {
        this.triggerPixel = triggerPixel;

        this.wings = wings;
        this.horn = horn;

        original = this;
    }

    Race(int triggerPixel, Race cloneOf) {
        cloneOf = cloneOf.getAlias();

        this.triggerPixel = triggerPixel;

        this.wings = cloneOf.wings;
        this.horn = cloneOf.horn;

        original = cloneOf;
    }

    /**
     * Returns true if this pony has a horn (and by extension can cast magic).
     * @return
     */
    public boolean hasHorn() {
        return horn;
    }

    /**
     * Returns true if this pony has wings. If it has wings, it can fly, of course.
     */
    public boolean hasWings() {
        return wings;
    }

    /**
     * Returns true if this is a human.
     */
    public boolean isHuman() {
        return this == HUMAN;
    }

    /**
     * Gets the original race that this one is an alias for, if one exists.
     * Otherwise returns this race.
     */
    @Nonnull
    public Race getAlias() {
        return original;
    }

    /**
     * Returns true if this race is a virtual one.
     */
    public boolean isVirtual() {
        return getAlias() != this;
    }

    /**
     * Gets the actual race determined by the given pony level.
     * PonyLevel.HUMANS would force all races to be humans.
     * PonyLevel.BOTH is no change.
     * PonyLevel.PONIES (should) return a pony if this is a human. Don't be fooled, though. It doesn't.
     */
    public Race getEffectiveRace(boolean ignorePony) {
        if (MineLittlePony.getInstance().getConfig().getEffectivePonyLevel(ignorePony) == PonyLevel.HUMANS) {
            return HUMAN;
        }

        return this;
    }

    @Override
    public int getTriggerPixel() {
        return triggerPixel;
    }
}
