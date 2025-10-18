package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.function.Function;
import java.util.function.Predicate;

public class PonyBodyPartFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>,
        N extends M
    > extends AbstractPonyFeature<S, M> {
    private final Predicate<? super M> visibilityTest;
    private final Function<M, SubModel<S>> bodyPart;

    public PonyBodyPartFeature(PonyRenderContext<?, S, M> context, Predicate<? super M> visibilityTest, Function<M, SubModel<S>> bodyPart) {
        super(context);
        this.visibilityTest = visibilityTest;
        this.bodyPart = bodyPart;
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        M model = getContextModel();
        if (visibilityTest.test(model)) {
            bodyPart.apply(model).render(model, state, matrices, queue);
        }
    }
}
