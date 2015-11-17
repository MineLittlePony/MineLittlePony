package com.brohoof.minelittlepony.model.pony.armor;

import com.brohoof.minelittlepony.model.ModelArmor;

public class pma_zombiePony extends ModelArmor {

    public pma_zombiePony(String path) {
        super(path);
        this.modelArmorChestplate = new pm_zombiePonyArmor(path);
        this.modelArmor = new pm_zombiePonyArmor(path);
    }
}
