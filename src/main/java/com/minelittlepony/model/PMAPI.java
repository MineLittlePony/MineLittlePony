package com.minelittlepony.model;

import com.minelittlepony.model.ponies.ModelHumanPlayer;
import com.minelittlepony.model.ponies.ModelIllagerPony;
import com.minelittlepony.model.ponies.ModelPlayerPony;
import com.minelittlepony.model.ponies.ModelSkeletonPony;
import com.minelittlepony.model.ponies.ModelVillagerPony;
import com.minelittlepony.model.ponies.ModelZombiePony;

import java.lang.reflect.Field;

/**
 * PMAPI - Pony Models API?
 *
 */
public final class PMAPI {

    public static final ModelWrapper
        pony = new ModelWrapper(new ModelPlayerPony(false)),
        ponySmall = new ModelWrapper(new ModelPlayerPony(true)),

        human = new ModelWrapper(new ModelHumanPlayer(false)),
        humanSmall = new ModelWrapper(new ModelHumanPlayer(true)),

        zombie = new ModelWrapper(new ModelZombiePony()),
        skeleton = new ModelWrapper(new ModelSkeletonPony()),
        villager = new ModelWrapper(new ModelVillagerPony()),
        illager = new ModelWrapper(new ModelIllagerPony());

    public static void init() {
        for (Field field : PMAPI.class.getFields()) {
            try {
                ModelWrapper model = (ModelWrapper) field.get(null);
                model.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
