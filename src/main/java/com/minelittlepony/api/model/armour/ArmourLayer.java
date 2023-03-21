package com.minelittlepony.api.model.armour;

/**
 * The layer used to render a given armour piece.
 */
public enum ArmourLayer {
    /**
     * Hanging loose and sagging free
     */
    OUTER,
    /**
     * Fits snugly to the player's model.
     */
    INNER;

    public int getLegacyId() {
        return ordinal() + 1;
    }

    public boolean isInner() {
        return this == INNER;
    }
}