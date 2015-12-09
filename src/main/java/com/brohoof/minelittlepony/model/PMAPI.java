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

    public static final PlayerModel pony = new PlayerModel(new pm_newPonyAdv()).setArmor(new pma_newPony());
    public static final PlayerModel zombie = new PlayerModel(new pm_zombiePony()).setArmor(new pma_zombiePony());
    public static final PlayerModel skeleton = new PlayerModel(new pm_skeletonPony()).setArmor(new pma_skeletonPony());
    public static final PlayerModel human = new PlayerModel(new pm_Human()).setArmor(new pma_Human());

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
