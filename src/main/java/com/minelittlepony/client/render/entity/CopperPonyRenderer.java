package com.minelittlepony.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

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

public class CopperPonyRenderer extends PonyRenderer<CopperGolem, CopperPonyRenderer.State, ClientPonyModel<CopperPonyRenderer.State>> {
    public static final Identifier BASE_TEXTURE = MineLittlePony.id("textures/entity/copper_golem/copper_golem_dragon.png");

    private static final TextureSupplier<CopperGolem> TEXTURES = entity -> {
        return MineLittlePony.id("textures/entity/copper_golem/" + getKey(entity.getWeatherState()) + "copper_golem_dragon.png");
    };
    private static final TextureSupplier<State> EYES_TEXTURES = state -> {
        return MineLittlePony.id("textures/entity/copper_golem/" + getKey(state.oxidationLevel) + "copper_golem_eyes_dragon.png");
    };

    private static String getKey(WeatheringCopper.WeatherState level) {
        return level == WeatheringCopper.WeatherState.UNAFFECTED ? "" : level.getSerializedName() + "_";
    }

    public CopperPonyRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.SPIKE, TEXTURES);
    }

    @Override
    protected void addFeatures(EntityRendererProvider.Context context) {
        super.addFeatures(context);
        addLayer(new GlowingEyesFeature<>(this, EYES_TEXTURES));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CopperGolem entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.oxidationLevel = entity.getWeatherState();
        state.scale += 0.13F;
        state.copperGolemState = entity.getState();
        state.spinHeadAnimationState.copyFrom(entity.getIdleAnimationState());
        state.gettingItemAnimationState.copyFrom(entity.getInteractionGetItemAnimationState());
        state.gettingNoItemAnimationState.copyFrom(entity.getInteractionGetNoItemAnimationState());
        state.droppingItemAnimationState.copyFrom(entity.getInteractionDropItemAnimationState());
        state.droppingNoItemAnimationState.copyFrom(entity.getInteractionDropNoItemAnimationState());
        state.headBlockItemStack = Optional.of(entity.getItemBySlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA)).flatMap(stack -> {
            if (stack.getItem() instanceof BlockItem block) {
                return Optional.of(stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(block.getBlock().defaultBlockState()));
            }
            return Optional.empty();
        });
    }

    public static class State extends PonyRenderState {
        public WeatheringCopper.WeatherState oxidationLevel = WeatheringCopper.WeatherState.UNAFFECTED;
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
