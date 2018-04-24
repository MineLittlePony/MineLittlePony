package com.minelittlepony.model;

import com.minelittlepony.IPonyData;

public abstract class AbstractArmor {

    public AbstractPonyModel modelArmorChestplate;
    public AbstractPonyModel modelArmor;

    public void apply(IPonyData meta) {
        modelArmorChestplate.metadata = meta;
        modelArmor.metadata = meta;
    }

}
