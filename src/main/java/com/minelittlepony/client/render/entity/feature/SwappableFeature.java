package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Function;
import java.util.function.Predicate;

public final class SwappableFeature<
        S extends EntityRenderState,
        M extends EntityModel<? super S>,
        P extends EntityRenderState,
        N extends EntityModel<? super P>
    > extends FeatureRenderer<S, M> {

    private final FeatureRenderer<S, M> normal;
    private final FeatureRenderer<P, N> swapped;

    private final Predicate<S> swapCondition;
    private final Function<S, P> stateConverter;

    public SwappableFeature(
            FeatureRendererContext<S, M> context,
            FeatureRenderer<S, M> normal,
            FeatureRenderer<P, N> swapped,
            Predicate<S> swapCondition,
            Function<S, P> stateConverter
    ) {
        super(context);
        this.normal = normal;
        this.swapped = swapped;
        this.swapCondition = swapCondition;
        this.stateConverter = stateConverter;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        if (swapCondition.test(state)) {
            swapped.render(matrices, vertices, light, stateConverter.apply(state), limbAngle, limbDistance);
        } else {
            normal.render(matrices, vertices, light, state, limbAngle, limbDistance);
        }
    }
}
