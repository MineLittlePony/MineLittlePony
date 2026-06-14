package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;

import com.minelittlepony.api.pony.DefaultPonySkinHelper;
import com.minelittlepony.api.state.PonifiedRenderState;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.BreezieModel;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.UUID;

/**
 * AKA a breezie :D
 */
public class AllayRenderer extends MobRenderer<Allay, AllayRenderer.State, BreezieModel> {
    public static final Identifier BREEZIE_PONIES = MineLittlePony.id("textures/entity/allay/pony");

    public AllayRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.ALLAY.createModel(), 0.4f);
        addLayer(new ItemInHandLayer<State, BreezieModel>(this));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(Allay entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, itemModelResolver, tickDelta);
        state.leftArmPose = state.leftHandItemState.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        state.rightArmPose = state.rightHandItemState.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
        state.uuid = entity.getUUID();
        state.dancing = entity.isDancing();
        state.spinning = entity.isSpinning();
        state.spinningAnimationTicks = entity.getSpinningProgress(tickDelta);
        state.itemHoldAnimationTicks = entity.getHoldingItemAnimationProgress(tickDelta);
    }

    @Override
    public Identifier getTextureLocation(State state) {
        return MineLittlePony.getInstance().getVariatedTextures().get(BREEZIE_PONIES).getId(state.uuid).orElse(DefaultPonySkinHelper.STEVE);
    }

    @Override
    protected void scale(State state, PoseStack matrices) {
        matrices.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected int getBlockLightLevel(Allay allayEntity, BlockPos blockPos) {
        return 15;
    }

    public static class State extends HumanoidRenderState implements PonifiedRenderState {
        public UUID uuid;
        public boolean dancing;
        public boolean spinning;
        public float spinningAnimationTicks;
        public float itemHoldAnimationTicks;

        @Override
        public boolean isOf(EntityType<?> entityType) {
            return this.entityType == entityType;
        }
    }
}
