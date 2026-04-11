package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractPonyFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends RenderLayer<S, M> {

    private final PonyRenderContext<?, S, M> context;

    public AbstractPonyFeature(PonyRenderContext<?, S, M> context) {
        super(context.upcast());
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    protected <T extends LivingEntity, C extends PonyRenderContext<T, S, M> & RenderLayerParent<S, M>> C getContext() {
        return (C)context;
    }

    public final Models<M> lookupModel(S state) {
        return getContext().lookupModel(state);
    }
}
