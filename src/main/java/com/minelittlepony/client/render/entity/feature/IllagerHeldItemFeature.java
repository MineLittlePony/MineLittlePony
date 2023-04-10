package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;

import com.minelittlepony.client.model.entity.race.AlicornModel;

public class IllagerHeldItemFeature<T extends IllagerEntity, M extends AlicornModel<T>> extends HeldItemFeature<T, M> {

    public IllagerHeldItemFeature(FeatureRendererContext<T,M> livingPony, HeldItemRenderer renderer) {
        super(livingPony, renderer);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        if (shouldRender(entity)) {
            super.render(stack, renderContext, lightUv, entity, limbDistance, limbAngle, tickDelta, age, headYaw, headPitch);
        }
    }

    public boolean shouldRender(T entity) {
        return entity.getState() != IllagerEntity.State.CROSSED;
    }
}
