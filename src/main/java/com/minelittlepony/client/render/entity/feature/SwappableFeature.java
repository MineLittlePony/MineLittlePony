package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Predicate;

public final class SwappableFeature<S extends EntityRenderState, M extends EntityModel<? super S>> extends FeatureRenderer<S, M> {

    private final FeatureRenderer<S, M> normal;
    private final FeatureRenderer<S, M> swapped;

    private final Predicate<S> swapCondition;

    public SwappableFeature(
            FeatureRendererContext<S, M> context,
            FeatureRenderer<S, M> normal,
            FeatureRenderer<S, M> swapped,
            Predicate<S> swapCondition
    ) {
        super(context);
        this.normal = normal;
        this.swapped = swapped;
        this.swapCondition = swapCondition;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        (swapCondition.test(state) ? swapped : normal).render(matrices, vertices, light, state, limbAngle, limbDistance);
    }
}
