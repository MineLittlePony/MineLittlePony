package com.minelittlepony.model;

import net.minecraft.util.EnumHandSide;

public interface IUnicorn<Arm> extends IModel {
    /**
     * Gets the arm used for holding items in their magic.
     */
    Arm getUnicornArmForSide(EnumHandSide side);

    /**
     * Returns true if this model is being applied to a race that can use magic.
     */
    boolean canCast();

    /**
     * Returns true if this model is currently using magic (horn is lit).
     * @return
     */
    boolean isCasting();

    /**
     * Gets the preferred magic color for this mode.
     */
    int getMagicColor();
}
