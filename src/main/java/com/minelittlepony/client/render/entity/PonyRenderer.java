package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelWithHorn;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Arm;

public abstract class PonyRenderer<
        T extends MobEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyRenderer<T, S, M> {
    protected static final float BASE_MODEL_SCALE = 15/16F;

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture) {
        this(context, key, texture, 1);
    }

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, key, texture, scale);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        super.addFeatures(context);
        addFeature(new StuckArrowsFeatureRenderer(this, context));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateRenderState(T entity, S state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.leftArmPose = getArmPose(state.leftArmPose, entity, Arm.LEFT);
        state.rightArmPose = getArmPose(state.rightArmPose, entity, Arm.RIGHT);
        state.hornGlowVisible = !IrisApiCompat.isOnShadowPass() && this.lookupModel(state).body() instanceof ModelWithHorn h && h.isCasting(state);
    }

    public BipedEntityModel.ArmPose getArmPose(BipedEntityModel.ArmPose initial, T entity, Arm arm) {
        return initial;
    }
}
