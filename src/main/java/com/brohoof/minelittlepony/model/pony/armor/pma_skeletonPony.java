package com.brohoof.minelittlepony.model.pony.armor;

import com.brohoof.minelittlepony.model.ModelArmor;

public class pma_skeletonPony extends ModelArmor {

    public pma_skeletonPony(String path) {
        super(path);
        this.modelArmorChestplate = new pm_skeletonPonyArmor(path);
        this.modelArmor = new pm_skeletonPonyArmor(path);
    }
}
