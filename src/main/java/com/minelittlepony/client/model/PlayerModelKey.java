package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.mson.api.*;

import java.util.function.*;

public record PlayerModelKey<M extends Model & PonyModel<?>> (
        ModelKey<M> steveKey,
        ModelKey<M> alexKey,
        MsonModel.Factory<AbstractPonyModel<?>> armorFactory
) {
    PlayerModelKey(String name,
            BiFunction<ModelPart, Boolean, M> modelFactory,
            MsonModel.Factory<AbstractPonyModel<?>> armorFactory
    ) {
        this(
            new ModelKeyImpl<>(MineLittlePony.id("races/steve/" + name), tree -> modelFactory.apply(tree, false)),
            new ModelKeyImpl<>(MineLittlePony.id("races/alex/" + name), tree -> modelFactory.apply(tree, true)),
            armorFactory
        );
    }

    public ModelKey<M> getKey(boolean slimArms) {
        return slimArms ? alexKey : steveKey;
    }

    public <N extends M> Models<N> create(boolean slimArms) {
        return create(slimArms, null);
    }

    public <N extends M> Models<N>  create(boolean slimArms, @Nullable Consumer<N> initializer) {
        return new Models<>(this, slimArms, initializer);
    }
}
