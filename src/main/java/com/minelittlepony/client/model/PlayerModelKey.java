package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.mson.api.*;

import java.util.function.*;

public record PlayerModelKey<M extends Model<?> & PonyModel<?>> (
        ModelKey<M> steveKey,
        ModelKey<M> alexKey,
        MsonModel.Factory<PonyModel<?>> armorFactory
) {
    PlayerModelKey(String name,
            BiFunction<ModelPart, Boolean, M> modelFactory,
            MsonModel.Factory<PonyModel<?>> armorFactory
    ) {
        this(
            new ModelKeyImpl<>(MineLittlePony.id("races/steve/" + name), tree -> modelFactory.apply(tree, false)),
            new ModelKeyImpl<>(MineLittlePony.id("races/alex/" + name), tree -> modelFactory.apply(tree, true)),
            armorFactory
        );
    }

    public <N extends M> Models<N>  alex() {
        return new Models<N>(alexKey, armorFactory);
    }

    public <N extends M> Models<N>  steve() {
        return new Models<N>(steveKey, armorFactory);
    }

    public <N extends M> Models<N>  create(boolean slimArms) {
        return slimArms ? alex() : steve();
    }
}
