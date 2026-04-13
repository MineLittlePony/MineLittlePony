package com.minelittlepony.api.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelKeyImpl;
import com.minelittlepony.mson.api.*;

import java.util.function.*;

public record PlayerModelKey<M extends Model<?> & PonyModel<?>> (
        ModelKey<M> steveKey,
        ModelKey<M> alexKey,
        MsonModel.Factory<PonyModel<?>> armorFactory
) {
    public PlayerModelKey(String name,
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
