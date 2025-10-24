package com.minelittlepony.client.render.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.entity.feature.GlowingEyesFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.Optional;

public class CopperPonyRenderer extends PonyRenderer<CopperGolemEntity, CopperPonyRenderer.State, ClientPonyModel<CopperPonyRenderer.State>> {
    public static final Identifier BASE_TEXTURE = MineLittlePony.id("textures/entity/copper_golem/copper_golem_dragon.png");

    private static final TextureSupplier<CopperGolemEntity> TEXTURES = entity -> {
        return MineLittlePony.id("textures/entity/copper_golem/" + getKey(entity.getOxidationLevel()) + "copper_golem_dragon.png");
    };
    private static final TextureSupplier<State> EYES_TEXTURES = state -> {
        return MineLittlePony.id("textures/entity/copper_golem/" + getKey(state.oxidationLevel) + "copper_golem_eyes_dragon.png");
    };

    private static String getKey(OxidationLevel level) {
        return level == OxidationLevel.UNAFFECTED ? "" : level.asString() + "_";
    }

    public CopperPonyRenderer(Context context) {
        super(context, ModelType.SPIKE, TEXTURES);
    }

    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        super.addFeatures(context);
        addFeature(new GlowingEyesFeature<>(this, EYES_TEXTURES));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(CopperGolemEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.oxidationLevel = entity.getOxidationLevel();
        state.baseScale += 0.13F;
        state.copperGolemState = entity.getState();
        state.spinHeadAnimationState.copyFrom(entity.getSpinHeadAnimationState());
        state.gettingItemAnimationState.copyFrom(entity.getGettingItemAnimationState());
        state.gettingNoItemAnimationState.copyFrom(entity.getGettingNoItemAnimationState());
        state.droppingItemAnimationState.copyFrom(entity.getDroppingItemAnimationState());
        state.droppingNoItemAnimationState.copyFrom(entity.getDroppingNoItemAnimationState());
        state.headBlockItemStack = Optional.of(entity.getEquippedStack(CopperGolemEntity.POPPY_SLOT)).flatMap(stack -> {
            if (stack.getItem() instanceof BlockItem block) {
                return Optional.of(stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT).applyToState(block.getBlock().getDefaultState()));
            }
            return Optional.empty();
        });
    }

    public static class State extends PonyRenderState {
        public OxidationLevel oxidationLevel = OxidationLevel.UNAFFECTED;
        public CopperGolemState copperGolemState = CopperGolemState.IDLE;
        public final AnimationState spinHeadAnimationState = new AnimationState();
        public final AnimationState gettingItemAnimationState = new AnimationState();
        public final AnimationState gettingNoItemAnimationState = new AnimationState();
        public final AnimationState droppingItemAnimationState = new AnimationState();
        public final AnimationState droppingNoItemAnimationState = new AnimationState();
        public Optional<BlockState> headBlockItemStack = Optional.empty();

        @Override
        protected Size computeSize(@Nullable LivingEntity entity, Size size) {
            return SizePreset.NORMAL;
        }
    }
}
