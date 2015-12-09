package com.brohoof.minelittlepony;

public enum PonyRace {
    EARTH(false, false),
    PEGASUS(true, false),
    UNICORN(false, true),
    ALICORN(true, true),
    CHANGELING(true, true),
    ZEBRA(false, false);

    private boolean wings;
    private boolean horn;

    private PonyRace(boolean wings, boolean horn) {
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
