package com.brohoof.minelittlepony.model;

import java.lang.reflect.Field;

import com.brohoof.minelittlepony.model.pony.pm_Human;
import com.brohoof.minelittlepony.model.pony.pm_newPonyAdv;
import com.brohoof.minelittlepony.model.pony.pm_skeletonPony;
import com.brohoof.minelittlepony.model.pony.pm_zombiePony;
import com.brohoof.minelittlepony.model.pony.armor.pma_Human;
import com.brohoof.minelittlepony.model.pony.armor.pma_newPony;
import com.brohoof.minelittlepony.model.pony.armor.pma_skeletonPony;
import com.brohoof.minelittlepony.model.pony.armor.pma_zombiePony;

public final class PMAPI {

    public static PlayerModel newPonyAdv_32 = new PlayerModel(new pm_newPonyAdv())
            .setTextureHeight(32)
            .setArmor(new pma_newPony());
    public static PlayerModel newPonyAdv = new PlayerModel(new pm_newPonyAdv())
            .setArmor(new pma_newPony());
    public static PlayerModel zombiePony = new PlayerModel(new pm_zombiePony())
            .setTextureHeight(32)
            .setArmor(new pma_zombiePony());
    public static PlayerModel skeletonPony = new PlayerModel(new pm_skeletonPony())
            .setTextureHeight(32)
            .setArmor(new pma_skeletonPony());
    public static PlayerModel human = new PlayerModel(new pm_Human())
            .setArmor(new pma_Human());

    public static void init() {
        for (Field field : PMAPI.class.getFields()) {
            try {
                PlayerModel model = (PlayerModel) field.get(null);
                model.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
