package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractPonyFeature<
        S extends PonyRenderState,
        M extends EntityModel<? super S> & PonyModel<S>
    > extends FeatureRenderer<S, M> {

    private final PonyRenderContext<?, S, M> context;

    @SuppressWarnings("unchecked")
    public AbstractPonyFeature(PonyRenderContext<?, S, M> context) {
        super((FeatureRendererContext<S, M>)context);
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    protected <T extends LivingEntity, C extends PonyRenderContext<T, S, M> & FeatureRendererContext<S, M>> C getContext() {
        return (C)context;
    }

    @Override
    public final M getContextModel() {
        return context.getInternalRenderer().getModels().body();
    }

    protected Models<?, M> getModelWrapper() {
        return context.getInternalRenderer().getModels();
    }
}
