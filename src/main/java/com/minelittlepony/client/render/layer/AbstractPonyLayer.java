package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.render.IPonyRender;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractPonyLayer<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends FeatureRenderer<T, M> {

    private final IPonyRender<T, M> context;

    @SuppressWarnings("unchecked")
    public AbstractPonyLayer(IPonyRender<T, M> context) {
        super((FeatureRendererContext<T, M>)context);
        this.context = context;
    }

    /**
     * Renders this layer.
     *
     * @param entity       The entity we're being called for.
     * @param move         Entity motion parameter - i.e. velocity in no specific direction used in bipeds to calculate step amount.
     * @param swing        Degree to which each 'limb' swings.
     * @param partialTicks Render partial ticks
     * @param ticks        Total whole and partial ticks since the entity's existance. Used in animations together with {@code swing} and {@code move}.
     * @param headYaw      Horizontal head motion in radians.
     * @param headPitch    Vertical head motion in radians.
     * @param scale        Scaling factor used to render this model. Determined by the return value of {@link RenderLivingBase.prepareScale}. Usually {@code 0.0625F}.
     */
    @Override
    public abstract void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale);

    @SuppressWarnings("unchecked")
    protected <C extends IPonyRender<T, M> & FeatureRendererContext<T, M>> C getContext() {
        return (C)context;
    }

    public M getPlayerModel() {
        return getContext().getModelWrapper().getBody();
    }

    public M getMainModel() {
        return getContext().getModel();
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
