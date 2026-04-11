package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelWithHorn;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;

public abstract class PonyRenderer<
        T extends Mob,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyRenderer<T, S, M> {
    protected static final float BASE_MODEL_SCALE = 15/16F;

    public PonyRenderer(EntityRendererProvider.Context context, ModelKey<? super M> key, TextureSupplier<T> texture) {
        this(context, key, texture, 1);
    }

    public PonyRenderer(EntityRendererProvider.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, key, texture, scale);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererProvider.Context context) {
        super.addFeatures(context);
        addLayer(new ArrowLayer(this, context));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void extractRenderState(T entity, S state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.leftArmPose = getArmPose(state.leftArmPose, entity, HumanoidArm.LEFT);
        state.rightArmPose = getArmPose(state.rightArmPose, entity, HumanoidArm.RIGHT);
        state.hornGlowVisible = !IrisApiCompat.isOnShadowPass() && lookupModel(state).body() instanceof ModelWithHorn h && h.isCasting(state);
    }

    public HumanoidModel.ArmPose getArmPose(HumanoidModel.ArmPose initial, T entity, HumanoidArm arm) {
        return initial;
    }
}
