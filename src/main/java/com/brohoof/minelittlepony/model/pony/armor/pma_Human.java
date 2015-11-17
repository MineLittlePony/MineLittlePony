package com.brohoof.minelittlepony.model.pony.armor;

import com.brohoof.minelittlepony.model.ModelArmor;
import com.brohoof.minelittlepony.model.pony.pm_Human;

public class pma_Human extends ModelArmor {

    public pma_Human(String path) {
        super(path);
        this.modelArmorChestplate = new pm_Human(path);
        this.modelArmor = new pm_Human(path);
    }

}
