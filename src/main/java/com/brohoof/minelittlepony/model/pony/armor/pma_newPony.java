package com.brohoof.minelittlepony.model.pony.armor;

import com.brohoof.minelittlepony.model.ModelArmor;

public class pma_newPony extends ModelArmor {
    public pma_newPony(String path) {
        super(path);
        this.modelArmorChestplate = new pm_newPonyArmor(path);
        this.modelArmor = new pm_newPonyArmor(path);
    }
}
