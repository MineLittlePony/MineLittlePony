package com.minelittlepony.model;

import com.minelittlepony.model.player.*;
import com.minelittlepony.model.ponies.*;

/**
 * PMAPI - Pony Models API?
 *
 * TODO: Remove this, move the models to where they're being used.
 */
public interface PMAPI {

    ModelWrapper earthpony = new ModelWrapper(new ModelEarthPony(false));
    ModelWrapper earthponySmall = new ModelWrapper(new ModelEarthPony(true));

    ModelWrapper pegasus = new ModelWrapper(new ModelPegasus(false));
    ModelWrapper pegasusSmall = new ModelWrapper(new ModelPegasus(true));

    ModelWrapper bat = new ModelWrapper(new ModelBatpony(false));
    ModelWrapper batSmall = new ModelWrapper(new ModelBatpony(true));

    ModelWrapper unicorn = new ModelWrapper(new ModelUnicorn(false));
    ModelWrapper unicornSmall = new ModelWrapper(new ModelUnicorn(true));

    ModelWrapper alicorn = new ModelWrapper(new ModelAlicorn(false));
    ModelWrapper alicornSmall = new ModelWrapper(new ModelAlicorn(true));

    ModelWrapper zebra = new ModelWrapper(new ModelZebra(false));
    ModelWrapper zebraSmall = new ModelWrapper(new ModelZebra(true));

    ModelWrapper seapony = new ModelWrapper(new ModelSeapony());

    ModelWrapper zombie = new ModelWrapper(new ModelZombiePony());
    ModelWrapper skeleton = new ModelWrapper(new ModelSkeletonPony());
    ModelWrapper villager = new ModelWrapper(new ModelVillagerPony());
    ModelWrapper illager = new ModelWrapper(new ModelIllagerPony());
    ModelWrapper witch = new ModelWrapper(new ModelWitchPony());
    ModelWrapper enderman = new ModelWrapper(new ModelEnderStallion());
}
