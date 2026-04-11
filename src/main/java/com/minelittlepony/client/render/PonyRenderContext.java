package com.minelittlepony.client.render;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.Untyped;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;

public interface PonyRenderContext<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends Gear.Context<S, M> {

    Pony getEntityPony(T entity);

    EquineRenderManager<T, S, M> getEquineManager();

    default Models<M> lookupModel(EntityRenderState state) {
        return getEquineManager().lookupModel(state);
    }

    @Override
    default Identifier getDefaultTexture(S entity, Wearable wearable) {
        return wearable.getDefaultTexture();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    default EntityRenderer<T, S> getVanillaRenderer() {
        return this instanceof EntityRenderer ? (EntityRenderer<T, S>)(Object)this : null;
    }

    default <S2 extends EntityRenderState, M2 extends EntityModel<? super S2>> RenderLayerParent<S2, M2> upcast() {
        return Untyped.cast(this);
    }
}
