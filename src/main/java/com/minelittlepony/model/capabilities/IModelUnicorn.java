package com.minelittlepony.model.capabilities;

import com.minelittlepony.render.PonyRenderer;

import net.minecraft.util.EnumHandSide;

public interface IModelUnicorn {
    PonyRenderer getUnicornArmForSide(EnumHandSide side);

    boolean isCasting();
}
