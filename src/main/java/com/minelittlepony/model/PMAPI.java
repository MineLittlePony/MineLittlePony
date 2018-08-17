package com.minelittlepony.model;

import com.minelittlepony.model.player.*;
import com.minelittlepony.model.ponies.*;

/**
 * PMAPI - Pony Models API?
 *
 * TODO: Remove this, move the models to where they're being used.
 */
public final class PMAPI {

    public static final ModelWrapper earthpony = new ModelWrapper(new ModelEarthPony(false));
    public static final ModelWrapper earthponySmall = new ModelWrapper(new ModelEarthPony(true));

    public static final ModelWrapper pegasus = new ModelWrapper(new ModelPegasus(false));
    public static final ModelWrapper pegasusSmall = new ModelWrapper(new ModelPegasus(true));

    public static final ModelWrapper unicorn = new ModelWrapper(new ModelUnicorn(false));
    public static final ModelWrapper unicornSmall = new ModelWrapper(new ModelUnicorn(true));

    public static final ModelWrapper alicorn = new ModelWrapper(new ModelAlicorn(false));
    public static final ModelWrapper alicornSmall = new ModelWrapper(new ModelAlicorn(true));

    public static final ModelWrapper zebra = new ModelWrapper(new ModelZebra(false));
    public static final ModelWrapper zebraSmall = new ModelWrapper(new ModelZebra(true));

    public static final ModelWrapper seapony = new ModelWrapper(new ModelSeapony());

    public static final ModelWrapper zombie = new ModelWrapper(new ModelZombiePony());
    public static final ModelWrapper skeleton = new ModelWrapper(new ModelSkeletonPony());
    public static final ModelWrapper villager = new ModelWrapper(new ModelVillagerPony());
    public static final ModelWrapper illager = new ModelWrapper(new ModelIllagerPony());
    public static final ModelWrapper witch = new ModelWrapper(new ModelWitchPony());
    public static final ModelWrapper enderman = new ModelWrapper(new ModelEnderStallion());
}
