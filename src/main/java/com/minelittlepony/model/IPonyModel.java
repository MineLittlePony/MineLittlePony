package com.minelittlepony.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;

public interface IPonyModel<T extends LivingEntity> extends PonyModelConstants, IModel, ICapitated<Cuboid>, ICompartmented<Cuboid> {

}
