package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.PonyRenderContext;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractPonyFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends FeatureRenderer<T, M> {

    private final PonyRenderContext<T, M> context;

    @SuppressWarnings("unchecked")
    public AbstractPonyFeature(PonyRenderContext<T, M> context) {
        super((FeatureRendererContext<T, M>)context);
        this.context = context;
    }

    /**
     * Renders this layer.
     *
     * @param stack        The GL transformation matrix
     * @param vertices     The output 3D vertex buffer
     * @param lightUv      The current light value
     * @param entity       The entity we're being called for.
     * @param limbDistance Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param limbAngle    Degree to which each 'limb' swings.
     * @param tickDelta    Render partial ticks
     * @param age          Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param headYaw      Horizontal head motion in radians.
     * @param headPitch    Vertical head motion in radians.
     */
    @Override
    public abstract void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch);

    @SuppressWarnings("unchecked")
    protected <C extends PonyRenderContext<T, M> & FeatureRendererContext<T, M>> C getContext() {
        return (C)context;
    }

    @Override
    public final M getContextModel() {
        return context.getInternalRenderer().getModels().body();
    }

    protected Models<T, M> getModelWrapper() {
        return context.getInternalRenderer().getModels();
    }
}
