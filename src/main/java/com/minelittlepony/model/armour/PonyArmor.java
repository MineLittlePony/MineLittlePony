package com.minelittlepony.model.armour;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.pony.data.IPonyData;

public class PonyArmor {

    public final AbstractPonyModel chestplate;
    public final AbstractPonyModel armour;

    public PonyArmor(AbstractPonyModel chest, AbstractPonyModel body) {
        chestplate = chest;
        armour = body;
    }

    public void apply(IPonyData meta) {
        chestplate.metadata = meta;
        armour.metadata = meta;
    }

    public void init() {
        chestplate.init(0, 1);
        armour.init(0, 0.5f);
    }
}
