package com.brohoof.minelittlepony.model;

import com.brohoof.minelittlepony.PonyData;

public abstract class AbstractArmor {

    public AbstractPonyModel modelArmorChestplate;
    public AbstractPonyModel modelArmor;

    public float layer() {
        return 1;
    }

    public int subimage(int slot) {
        return slot == 2 ? 2 : 1;
    }

    public void apply(PonyData meta) {
        modelArmorChestplate.metadata = meta;
        modelArmor.metadata = meta;
    }

}
