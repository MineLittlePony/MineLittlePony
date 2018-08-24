package com.minelittlepony.pony.data;

import com.minelittlepony.model.player.PlayerModels;

public enum PonyRace implements ITriggerPixelMapped<PonyRace> {

    HUMAN(0, PlayerModels.DEFAULT, false, false),
    EARTH(0xf9b131, PlayerModels.EARTH,false, false),
    PEGASUS(0x88caf0, PlayerModels.PEGASUS, true, false),
    UNICORN(0xd19fe4, PlayerModels.UNICORN, false, true),
    ALICORN(0xfef9fc, PlayerModels.ALICORN, true, true),
    CHANGELING(0x282b29, PlayerModels.ALICORN, true, true),
    ZEBRA(0xd0cccf, PlayerModels.ZEBRA, false, false),
    REFORMED_CHANGELING(0xcaed5a, PlayerModels.ALICORN, true, true),
    GRIFFIN(0xae9145, PlayerModels.PEGASUS, true, false),
    HIPPOGRIFF(0xd6ddac, PlayerModels.PEGASUS, true, false),
    BATPONY(0xdddddd, PlayerModels.BATPONY, true, false),
    SEAPONY(0x3655dd, PlayerModels.SEAPONY, false, true);

    private boolean wings;
    private boolean horn;

    private int triggerPixel;

    private PlayerModels model;

    PonyRace(int triggerPixel, PlayerModels model, boolean wings, boolean horn) {
        this.triggerPixel = triggerPixel;

        this.wings = wings;
        this.horn = horn;
        this.model = model;
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
     * Gets the model type associated with this species.
     */
    public PlayerModels getModel() {
        return model;
    }

    /**
     * Gets the actual race determined by the given pony level.
     * PonyLevel.HUMANS would force all races to be humans.
     * PonyLevel.BOTH is no change.
     * PonyLevel.PONIES (should) return a pony if this is a human. Don't be fooled, though. It doesn't.
     */
    public PonyRace getEffectiveRace(PonyLevel level) {
        if (level == PonyLevel.HUMANS) return HUMAN;
        return this;
    }

    @Override
    public int getTriggerPixel() {
        return triggerPixel;
    }
}
