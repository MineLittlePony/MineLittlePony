package com.minelittlepony.model.capabilities;

import net.minecraft.util.EnumHandSide;

import com.minelittlepony.render.model.PonyRenderer;

public interface IModelUnicorn extends IModel {
    /**
     * Gets the arm used for holding items in their magic.
     */
    PonyRenderer getUnicornArmForSide(EnumHandSide side);

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
