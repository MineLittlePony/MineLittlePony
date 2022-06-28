package com.minelittlepony.api.model;

public interface IUnicorn extends IModel {
    /**
     * Returns true if this model is being applied to a race that can use magic.
     */
    default boolean canCast() {
        return getMetadata().hasMagic();
    }

    /**
     * Returns true if this model has an visible horns.
     */
    default boolean hasHorn() {
        return getMetadata().hasHorn();
    }

    /**
     * Returns true if this model is currently using magic (horn is lit).
     * @return
     */
    boolean isCasting();

    /**
     * Gets the preferred magic color for this mode.
     */
    default int getMagicColor() {
        return getMetadata().getGlowColor();
    }
}
