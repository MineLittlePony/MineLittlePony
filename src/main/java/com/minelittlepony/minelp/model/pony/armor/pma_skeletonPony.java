package com.minelittlepony.minelp.model.pony.armor;

import com.minelittlepony.minelp.model.ModelArmor;

public class pma_skeletonPony extends ModelArmor {

    public pma_skeletonPony(String path) {
        super(path);
        this.modelArmorChestplate = new pm_skeletonPonyArmor(path);
        this.modelArmor = new pm_skeletonPonyArmor(path);
    }
}
