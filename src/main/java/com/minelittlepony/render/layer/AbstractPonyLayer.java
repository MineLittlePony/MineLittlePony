package com.minelittlepony.render.layer;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ponies.ModelHumanPlayer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public abstract class AbstractPonyLayer<T extends EntityLivingBase> implements LayerRenderer<EntityLivingBase> {

    private final RenderLivingBase<T> renderer;
    private LayerRenderer<? super T> layer;

    public AbstractPonyLayer(RenderLivingBase<T> renderer, LayerRenderer<? super T> humanLayer) {
        this.renderer = renderer;
        this.layer = humanLayer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void doRenderLayer(EntityLivingBase entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {
        ModelBase model = renderer.getMainModel();
        if (model instanceof ModelHumanPlayer) {
            // render the human layer
            layer.doRenderLayer((T)entity, move, swing, ticks, age, headYaw, headPitch, scale);
        } else {
            // render the pony layer
            doPonyRender((T)entity, move, swing, ticks, age, headYaw, headPitch, scale);
        }
    }

    protected abstract void doPonyRender(T entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale);

    protected RenderLivingBase<T> getRenderer() {
        return renderer;
    }

    public AbstractPonyModel getPlayerModel() {
        return ((IRenderPony) getRenderer()).getPlayerModel().getModel();
    }

    public AbstractPonyModel getPonyModel() {
        return getMainModel();
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
