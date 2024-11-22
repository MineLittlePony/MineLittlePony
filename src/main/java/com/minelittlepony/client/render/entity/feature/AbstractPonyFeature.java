package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractPonyFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends FeatureRenderer<S, M> {

    private final PonyRenderContext<?, S, M> context;

    public AbstractPonyFeature(PonyRenderContext<?, S, M> context) {
        super(context.upcast());
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    protected <T extends LivingEntity, C extends PonyRenderContext<T, S, M> & FeatureRendererContext<S, M>> C getContext() {
        return (C)context;
    }

    @Override
    public final M getContextModel() {
        return context.getEquineManager().getModels().body();
    }

    protected Models<M> getModelWrapper() {
        return context.getEquineManager().getModels();
    }
}
