package com.minelittlepony.model;

import com.minelittlepony.PonyData;

public abstract class AbstractArmor {

    public AbstractPonyModel modelArmorChestplate;
    public AbstractPonyModel modelArmor;

    public void apply(PonyData meta) {
        modelArmorChestplate.metadata = meta;
        modelArmor.metadata = meta;
    }

}
