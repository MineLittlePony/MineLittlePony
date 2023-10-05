package com.minelittlepony.client.render;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.Pony;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

public interface PonyRenderContext<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends Gear.Context<T, M> {

    Pony getEntityPony(T entity);

    EquineRenderManager<T, M> getInternalRenderer();

    void setModel(M model);
}
