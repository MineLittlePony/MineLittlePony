package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.feature.ClothingFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Supplier;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.skeleton.*;

import org.jetbrains.annotations.Nullable;

public class SkeleponyRenderer<T extends AbstractSkeleton, S extends SkeleponyRenderer.State> extends PonyRenderer<T, S, AlicornModel<S>> {
    public static final Identifier SKELETON = MineLittlePony.id("textures/entity/skeleton/skeleton_pony.png");
    public static final Identifier WITHER = MineLittlePony.id("textures/entity/skeleton/skeleton_wither_pony.png");
    public static final Identifier STRAY = MineLittlePony.id("textures/entity/skeleton/stray_pony.png");
    public static final Identifier PARCHED = MineLittlePony.id("textures/entity/skeleton/parched_pony.png");
    public static final Identifier BOGGED = MineLittlePony.id("textures/entity/skeleton/bogged_pony.png");

    public static final Identifier STRAY_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/stray_pony_overlay.png");
    public static final Identifier PARCHED_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/parched_pony_overlay.png");
    public static final Identifier BOGGED_SKELETON_OVERLAY = MineLittlePony.id("textures/entity/skeleton/bogged_pony_overlay.png");

    private final Supplier<S> stateFactory;

    public SkeleponyRenderer(EntityRendererProvider.Context context, Identifier texture, float scale, Supplier<S> stateFactory) {
        super(context, ModelType.SKELETON, TextureSupplier.of(texture), scale);
        this.stateFactory = stateFactory;
    }

    @Override
    public final S createRenderState() {
        return stateFactory.get();
    }

    public static SkeleponyRenderer<Skeleton, State> skeleton(EntityRendererProvider.Context context) {
        return new SkeleponyRenderer<>(context, SKELETON, 1, State::new);
    }

    public static SkeleponyRenderer<Stray, State> stray(EntityRendererProvider.Context context) {
        return PonyRenderer.appendFeature(new SkeleponyRenderer<Stray, State>(context, STRAY, 1, State::new), ctx -> {
            return new ClothingFeature<State, AlicornModel<State>>(ctx, ModelType.SKELETON_CLOTHES, STRAY_SKELETON_OVERLAY);
        });
    }

    public static SkeleponyRenderer<Bogged, BoggedState> bogged(EntityRendererProvider.Context context) {
        return PonyRenderer.appendFeature(PonyRenderer.appendFeature(new SkeleponyRenderer<>(context, BOGGED, 1, BoggedState::new), ctx -> {
            return new ClothingFeature<BoggedState, AlicornModel<BoggedState>>(ctx, ModelType.SKELETON_CLOTHES, BOGGED_SKELETON_OVERLAY);
        }), BoggedMushroomsFeature::new);
    }

    public static SkeleponyRenderer<Parched, State> parched(EntityRendererProvider.Context context) {
        return PonyRenderer.appendFeature(new SkeleponyRenderer<Parched, State>(context, PARCHED, 1, State::new), ctx -> {
            return new ClothingFeature<State, AlicornModel<State>>(ctx, ModelType.SKELETON_CLOTHES, PARCHED_SKELETON_OVERLAY);
        });
    }

    public static SkeleponyRenderer<WitherSkeleton, State> wither(EntityRendererProvider.Context context) {
        return new SkeleponyRenderer<>(context, WITHER, 1.2F, State::new);
    }

    public static class BoggedMushroomsFeature<
        T extends AbstractSkeleton,
        S extends SkeleponyRenderer.State
    > extends RenderLayer<BoggedState, AlicornModel<BoggedState>> {
        public static final Identifier MUSHROOMS = MineLittlePony.id("textures/entity/skeleton/bogged_pony_mushrooms.png");

        private final Model.Simple model = ModelType.BOGGED_MUSHROOMS.createModel();

        public BoggedMushroomsFeature(LivingEntityRenderer<Bogged, BoggedState, AlicornModel<BoggedState>> renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, BoggedState state, float yRot, float xRot) {
            if (!state.sheared) {
                matrices.pushPose();
                getParentModel().transformAccessory(state, BodyPart.HEAD, matrices);
                queue.submitModel(model, Unit.INSTANCE, matrices, model.renderType(MUSHROOMS), light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
                matrices.popPose();
            }
        }
    }

    public static class BoggedState extends State {
        public boolean sheared;

        @Override
        public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            sheared = entity instanceof Bogged bogged && bogged.isSheared();
        }
    }

    public static class State extends PonyRenderState {
        public boolean isAttacking;

        @Override
        public void updateState(ItemModelResolver resolver, LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
            super.updateState(resolver, entity, models, pony, mode);
            isAttacking = entity instanceof Mob h && h.isAggressive();
        }

        @Override
        protected Race computeRace(@Nullable LivingEntity entity, Pony pony) {
            return entity != null && entity.getUUID().getLeastSignificantBits() % 3 == 0 ? Race.EARTH : Race.UNICORN;
        }

        @Override
        protected float getLegOutset() {
            if (attributes.isLyingDown) return 2.6f;
            if (attributes.isCrouching) return 0;
            return 4;
        }
    }
}
