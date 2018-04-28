package com.minelittlepony.model;

import com.minelittlepony.model.player.ModelAlicorn;
import com.minelittlepony.model.player.ModelEarthPony;
import com.minelittlepony.model.player.ModelPegasus;
import com.minelittlepony.model.ponies.ModelIllagerPony;
import com.minelittlepony.model.ponies.ModelSkeletonPony;
import com.minelittlepony.model.ponies.ModelVillagerPony;
import com.minelittlepony.model.ponies.ModelWitchPony;
import com.minelittlepony.model.ponies.ModelZombiePony;

import java.lang.reflect.Field;

/**
 * PMAPI - Pony Models API?
 *
 */
public final class PMAPI {

    public static final ModelWrapper pony = new ModelWrapper(new ModelAlicorn(false));
    public static final ModelWrapper ponySmall = new ModelWrapper(new ModelAlicorn(true));

    public static final ModelWrapper earthpony = new ModelWrapper(new ModelEarthPony(false));
    public static final ModelWrapper earthponySmall = new ModelWrapper(new ModelEarthPony(true));

    public static final ModelWrapper pegasus = new ModelWrapper(new ModelPegasus(false));
    public static final ModelWrapper pegasusSmall = new ModelWrapper(new ModelPegasus(true));

    public static final ModelWrapper alicorn = new ModelWrapper(new ModelAlicorn(false));
    public static final ModelWrapper alicornSmall = new ModelWrapper(new ModelAlicorn(true));

    public static final ModelWrapper zombie = new ModelWrapper(new ModelZombiePony());
    public static final ModelWrapper skeleton = new ModelWrapper(new ModelSkeletonPony());
    public static final ModelWrapper villager = new ModelWrapper(new ModelVillagerPony());
    public static final ModelWrapper illager = new ModelWrapper(new ModelIllagerPony());
    public static final ModelWrapper witch = new ModelWrapper(new ModelWitchPony());

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
