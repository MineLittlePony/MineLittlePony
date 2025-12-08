package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.BreezieModel;
import com.minelittlepony.client.render.entity.state.PonifiedRenderState;

import java.util.UUID;

/**
 * AKA a breezie :D
 */
public class AllayRenderer extends MobEntityRenderer<AllayEntity, AllayRenderer.State, BreezieModel> {
    public static final Identifier BREEZIE_PONIES = MineLittlePony.id("textures/entity/allay/pony");

    public AllayRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ALLAY.createModel(), 0.4f);
        addFeature(new HeldItemFeatureRenderer<State, BreezieModel>(this));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    public void updateRenderState(AllayEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        ArmedEntityRenderState.updateRenderState(entity, state, itemModelResolver, tickDelta);
        state.leftArmPose = state.leftHandItemState.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        state.rightArmPose = state.rightHandItemState.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        state.uuid = entity.getUuid();
        state.dancing = entity.isDancing();
        state.spinning = entity.isSpinning();
        state.spinningAnimationTicks = entity.getSpinningAnimationTicks(tickDelta);
        state.itemHoldAnimationTicks = entity.getItemHoldAnimationTicks(tickDelta);
    }

    @Override
    public Identifier getTexture(State state) {
        return MineLittlePony.getInstance().getVariatedTextures().get(BREEZIE_PONIES, state.uuid).orElse(DefaultPonySkinHelper.STEVE);
    }

    @Override
    protected void scale(State state, MatrixStack matrices) {
        matrices.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }

    public static class State extends BipedEntityRenderState implements PonifiedRenderState {
        public UUID uuid;
        public boolean dancing;
        public boolean spinning;
        public float spinningAnimationTicks;
        public float itemHoldAnimationTicks;
    }
}
