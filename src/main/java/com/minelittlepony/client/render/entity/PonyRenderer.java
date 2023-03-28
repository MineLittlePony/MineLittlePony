package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.mson.api.ModelKey;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.entity.mob.MobEntity;

public class PonyRenderer<T extends MobEntity, M extends ClientPonyModel<T>> extends AbstractPonyRenderer<T, M> {
    protected static final float BASE_MODEL_SCALE = 15/16F;

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture) {
        this(context, key, texture, 1);
    }

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, key, texture, scale);
    }

    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        super.addFeatures(context);
        addFeature(new StuckArrowsFeatureRenderer<>(context, this));
    }
}
