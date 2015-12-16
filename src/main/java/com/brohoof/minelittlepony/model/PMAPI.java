package com.brohoof.minelittlepony.model;

import java.lang.reflect.Field;

import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;
import com.brohoof.minelittlepony.model.pony.ModelPlayerPony;
import com.brohoof.minelittlepony.model.pony.ModelSkeletonPony;
import com.brohoof.minelittlepony.model.pony.ModelVillagerPony;
import com.brohoof.minelittlepony.model.pony.ModelZombiePony;
import com.brohoof.minelittlepony.model.pony.armor.HumanArmors;
import com.brohoof.minelittlepony.model.pony.armor.PonyArmors;
import com.brohoof.minelittlepony.model.pony.armor.SkeletonPonyArmors;
import com.brohoof.minelittlepony.model.pony.armor.ZombiePonyArmors;

public final class PMAPI {

    public static final PlayerModel pony = new PlayerModel(new ModelPlayerPony()).setArmor(new PonyArmors());
    public static final PlayerModel zombie = new PlayerModel(new ModelZombiePony()).setArmor(new ZombiePonyArmors());
    public static final PlayerModel skeleton = new PlayerModel(new ModelSkeletonPony()).setArmor(new SkeletonPonyArmors());
    public static final PlayerModel villager = new PlayerModel(new ModelVillagerPony()).setArmor(new PonyArmors());
    public static final PlayerModel human = new PlayerModel(new ModelHumanPlayer()).setArmor(new HumanArmors());

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
