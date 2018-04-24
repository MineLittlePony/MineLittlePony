package com.minelittlepony;

import com.minelittlepony.model.PlayerModels;

public enum PonyRace {

    HUMAN(PlayerModels.HUMAN, false, false),
    EARTH(PlayerModels.PONY,false, false),
    PEGASUS(PlayerModels.PONY, true, false),
    UNICORN(PlayerModels.PONY, false, true),
    ALICORN(PlayerModels.PONY, true, true),
    CHANGELING(PlayerModels.PONY, true, true),
    ZEBRA(PlayerModels.PONY, false, false),
    REFORMED_CHANGELING(PlayerModels.PONY, true, true),
    GRIFFIN(PlayerModels.PONY, true, false),
    HIPPOGRIFF(PlayerModels.PONY, true, false);

    private boolean wings, horn;

    private PlayerModels model;

    PonyRace(PlayerModels model, boolean wings, boolean horn) {
        this.wings = wings;
        this.horn = horn;
        this.model = model;
    }

    public boolean hasHorn() {
        return horn;
    }

    public boolean hasWings() {
        return wings;
    }

    public boolean isHuman() {
        return this == HUMAN;
    }

    public PlayerModels getModel() {
        return model;
    }

    public PonyRace getEffectiveRace(PonyLevel level) {
        if (level == PonyLevel.HUMANS) return HUMAN;
        return this;
    }
}
