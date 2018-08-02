package com.minelittlepony.render.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModel;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public abstract class AbstractPonyLayer<T extends EntityLivingBase> implements LayerRenderer<EntityLivingBase> {

    private final RenderLivingBase<T> renderer;

    public AbstractPonyLayer(RenderLivingBase<T> renderer) {
        this.renderer = renderer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void doRenderLayer(EntityLivingBase entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        // render the pony layer
        doPonyRender((T)entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
    }

    /**
     * Renders this layer.
     *
     * @param entity       The entity we're being called for.
     * @param move         Entity motion parameter - i.e. velocity in no specific direction used in
     *                         bipeds to calculate step amount.
     * @param swing        Degree to which each 'limb' swings.
     * @param partialTicks Render partial ticks
     * @param ticks        Total whole and partial ticks since the entity's existance. Used in
     *                         animations together with {@code swing} and {@code move}.
     * @param headYaw      Horizontal head motion in radians.
     * @param headPitch    Vertical head motion in radians.
     * @param scale        Scaling factor used to render this model. Determined by the return value of
     *                         {@link RenderLivingBase.prepareScale}. Usually {@code 0.0625F}.
     */
    protected abstract void doPonyRender(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale);

    protected RenderLivingBase<T> getRenderer() {
        return renderer;
    }

    @SuppressWarnings("unchecked")
    protected IRenderPony<T> getPonyRenderer() {
        return (IRenderPony<T>)renderer;
    }

    public AbstractPonyModel getPlayerModel() {
        return getPonyRenderer().getModelWrapper().getBody();
    }

    @SuppressWarnings("unchecked")
    public <M extends IModel> M getPonyModel() {
        return (M)getMainModel();
    }

    @SuppressWarnings("unchecked")
    public <M extends ModelBase> M getMainModel() {
        return (M)getRenderer().getMainModel();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
