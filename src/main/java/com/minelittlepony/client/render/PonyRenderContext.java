package com.minelittlepony.client.render;

import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;

public interface PonyRenderContext<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends Gear.Context<S, M> {

    Pony getEntityPony(T entity);

    EquineRenderManager<T, S, M> getInternalRenderer();

    void setModel(M model);

    @Nullable
    @SuppressWarnings("unchecked")
    default EntityRenderer<T, S> getVanillaRenderer() {
        return this instanceof EntityRenderer ? (EntityRenderer<T, S>)(Object)this : null;
    }

    @SuppressWarnings("unchecked")
    default <S2 extends EntityRenderState, M2 extends EntityModel<? super S2>> FeatureRendererContext<S2, M2> upcast() {
        return (FeatureRendererContext<S2, M2>)this;
    }
}
