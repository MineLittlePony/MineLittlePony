package com.minelittlepony.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.pony.IPony;

public interface IPonyModel<T extends LivingEntity> extends PonyModelConstants, IModel, ICapitated<Cuboid>, ICompartmented<Cuboid> {
    default void updateLivingState(T entity, IPony pony) {

    }
}
