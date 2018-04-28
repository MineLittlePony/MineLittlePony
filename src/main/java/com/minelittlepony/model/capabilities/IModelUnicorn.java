package com.minelittlepony.model.capabilities;

import com.minelittlepony.render.PonyRenderer;

import net.minecraft.util.EnumHandSide;

public interface IModelUnicorn extends IModel {
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
}
