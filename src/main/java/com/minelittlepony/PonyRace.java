package com.minelittlepony;

public enum PonyRace {

    HUMAN(false, false),
    EARTH(false, false),
    PEGASUS(true, false),
    UNICORN(false, true),
    ALICORN(true, true),
    CHANGELING(true, true),
    ZEBRA(false, false),
    REFORMED_CHANGELING(true, true),
    GRIFFIN(true, false),
    HIPPOGRIFF(true, false),
    ;

    private boolean wings;
    private boolean horn;

    PonyRace(boolean wings, boolean horn) {
        this.wings = wings;
        this.horn = horn;
    }

    public boolean hasHorn() {
        return horn;
    }

    public boolean hasWings() {
        return wings;
    }
}
