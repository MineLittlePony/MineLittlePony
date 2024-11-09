package com.minelittlepony.client.render;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

public interface PonyRenderContext<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends EntityModel<? super S> & PonyModel<S>
    > extends Gear.Context<S, M> {

    Pony getEntityPony(T entity);

    EquineRenderManager<T, S, M> getInternalRenderer();

    void setModel(M model);
}
