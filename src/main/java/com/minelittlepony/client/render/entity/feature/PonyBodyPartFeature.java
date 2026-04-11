package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;

import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Function;
import java.util.function.Predicate;

public class PonyBodyPartFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {
    private final Predicate<? super M> visibilityTest;
    private final Function<M, SubModel<S>> bodyPart;

    public PonyBodyPartFeature(PonyRenderContext<?, S, M> context, Function<M, SubModel<S>> bodyPart, Predicate<? super M> visibilityTest) {
        super(context);
        this.visibilityTest = visibilityTest;
        this.bodyPart = bodyPart;
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, S state, float limbAngle, float limbDistance) {
        M model = getParentModel();
        if (visibilityTest.test(model)) {
            bodyPart.apply(model).render(model, state, matrices, queue);
        }
    }
}
