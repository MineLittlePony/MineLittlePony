package com.minelittlepony.model.armour;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.pony.data.IPonyData;

public class PonyArmor {

    public final AbstractPonyModel modelArmorChestplate, modelArmor;
    
    public PonyArmor(AbstractPonyModel chest, AbstractPonyModel body) {
        this.modelArmorChestplate = chest;
        this.modelArmor = body;
    }
    
    public void apply(IPonyData meta) {
        modelArmorChestplate.metadata = meta;
        modelArmor.metadata = meta;
    }

    public void init() {
        modelArmorChestplate.init(0, 1);
        modelArmor.init(0, 0.5f);
    }
}
