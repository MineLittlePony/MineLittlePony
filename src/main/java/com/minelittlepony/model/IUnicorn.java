package com.minelittlepony.model;

import net.minecraft.util.AbsoluteHand;

public interface IUnicorn<Arm> extends IModel {
    /**
     * Gets the arm used for holding items in their magic.
     */
    Arm getUnicornArmForSide(AbsoluteHand side);

    /**
     * Returns true if this model is being applied to a race that can use magic.
     */
    default boolean canCast() {
        return getMetadata().hasMagic();
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
