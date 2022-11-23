package com.minelittlepony.api.pony.meta;

import com.minelittlepony.api.pony.TriggerPixelType;

import java.util.Arrays;
import java.util.List;

public enum Race implements TriggerPixelType<Race> {
    HUMAN       (0x000000, false, false),
    EARTH       (0xf9b131, false, false),
    PEGASUS     (0x88caf0, true,  false),
    UNICORN     (0xd19fe4, false, true),
    ALICORN     (0xfef9fc, true,  true),
    CHANGELING  (0x282b29, true,  true),
    ZEBRA       (0xd0cccf, false, false),
    CHANGEDLING (0xcaed5a, true, true),
    GRYPHON     (0xae9145, true, false),
    HIPPOGRIFF  (0xd6ddac, true, false),
    KIRIN       (0xfa88af, false, true),
    BATPONY     (0xeeeeee, true,  false),
    SEAPONY     (0x3655dd, false, true);

    private boolean wings;
    private boolean horn;

    private int triggerPixel;

    public static final List<Race> REGISTRY = Arrays.asList(values());

    Race(int triggerPixel, boolean wings, boolean horn) {
        this.triggerPixel = triggerPixel;

        this.wings = wings;
        this.horn = horn;
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

    @Override
    public int getColorCode() {
        return triggerPixel;
    }

    public String getModelId(boolean isSlim) {
        if (isHuman()) {
            return isSlim ? "slim" : "default";
        }
        return isSlim ? "slim" + name().toLowerCase() : name().toLowerCase();
    }
}
