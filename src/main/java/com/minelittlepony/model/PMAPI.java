package com.minelittlepony.model;

import java.lang.reflect.Field;

import com.minelittlepony.model.pony.ModelHumanPlayer;
import com.minelittlepony.model.pony.ModelPlayerPony;
import com.minelittlepony.model.pony.ModelSkeletonPony;
import com.minelittlepony.model.pony.ModelVillagerPony;
import com.minelittlepony.model.pony.ModelZombiePony;
import com.minelittlepony.model.pony.armor.HumanArmors;
import com.minelittlepony.model.pony.armor.PonyArmors;
import com.minelittlepony.model.pony.armor.SkeletonPonyArmors;
import com.minelittlepony.model.pony.armor.ZombiePonyArmors;

public final class PMAPI {

    public static final PlayerModel pony = new PlayerModel(new ModelPlayerPony(false)).setArmor(new PonyArmors());
    public static final PlayerModel ponySmall = new PlayerModel(new ModelPlayerPony(true)).setArmor(new PonyArmors());
    public static final PlayerModel zombie = new PlayerModel(new ModelZombiePony()).setArmor(new ZombiePonyArmors());
    public static final PlayerModel skeleton = new PlayerModel(new ModelSkeletonPony()).setArmor(new SkeletonPonyArmors());
    public static final PlayerModel villager = new PlayerModel(new ModelVillagerPony()).setArmor(new PonyArmors());
    public static final PlayerModel human = new PlayerModel(new ModelHumanPlayer(false)).setArmor(new HumanArmors());
    public static final PlayerModel humanSmall = new PlayerModel(new ModelHumanPlayer(true)).setArmor(new HumanArmors());

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
