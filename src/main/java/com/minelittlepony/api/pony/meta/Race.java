package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;

import org.jetbrains.annotations.NotNull;

public enum Race implements TriggerPixelType<Race> {

    HUMAN       (0x000000, false, false),
    EARTH       (0xf9b131, false, false),
    PEGASUS     (0x88caf0, true,  false),
    UNICORN     (0xd19fe4, false, true),
    ALICORN     (0xfef9fc, true,  true),
    CHANGELING  (0x282b29, true,  true),
    ZEBRA       (0xd0cccf, false, false),
    CHANGEDLING (0xcaed5a, CHANGELING),
    GRYPHON     (0xae9145, PEGASUS),
    HIPPOGRIFF  (0xd6ddac, PEGASUS),
    KIRIN       (0xfa88af, UNICORN),
    BATPONY     (0xeeeeee, true,  false),
    SEAPONY     (0x3655dd, false, true);

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
    @NotNull
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
     * Returns true if both races resolve to the same value.
     */
    public boolean isEquivalentTo(Race other) {
        return getAlias() == other.getAlias();
    }

    @Override
    public int getColorCode() {
        return triggerPixel;
    }
}
