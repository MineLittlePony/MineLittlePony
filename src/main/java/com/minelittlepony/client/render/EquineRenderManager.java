package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.*;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.PonyDataLoader;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.transform.PonyPosture;
import com.minelittlepony.mson.api.ModelKey;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;

import org.jetbrains.annotations.Nullable;

public class EquineRenderManager<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>> {

    private Function<Race, Models<M>> modelsLookup;

    private final PonyRenderContext<T, S, M> context;
    private final Transformer<? super S> transformer;

    public EquineRenderManager(PonyRenderContext<T, S, M> context, Transformer<? super S> transformer, Function<Race, Models<M>> modelsLookup) {
        this.context = context;
        this.transformer = transformer;
        setModelsLookup(modelsLookup);
    }

    public EquineRenderManager(PonyRenderContext<T, S, M> context, Transformer<? super S> transformer, Models<M> models) {
        this.context = context;
        this.transformer = transformer;
        setModelsLookup(race -> models);
    }

    public EquineRenderManager(PonyRenderContext<T, S, M> context, Transformer<? super S> transformer, ModelKey<? super M> key) {
        this(context, transformer, new Models<>(key));
    }

    public void setModelsLookup(Function<Race, Models<M>> modelsLookup) {
        this.modelsLookup = Util.memoize(modelsLookup);
    }

    public Models<M> lookupModel(EntityRenderState state) {
        return lookupModel(state instanceof PonyModel.AttributedHolder holder ? holder.getRace() : Race.EARTH);
    }

    public Models<M> lookupModel(PonyRenderState state) {
        return lookupModel(state.getRace());
    }

    public Models<M> lookupModel(Race race) {
        return modelsLookup.apply(race);
    }

    public Box getBoundingBox(T entity, Box box) {

        if (entity.isSleeping() || !PonyConfig.getInstance().frustrum.get()) {
            return box;
        }

        Pony pony = context.getEntityPony(entity);
        float scale = (entity.isBaby() ? SizePreset.FOAL : pony.size()).scaleFactor();
        return DebugBoundingBoxRenderer.applyScale(scale, box);
    }

    public Box getHitbox(T entity) {
        Pony pony = context.getEntityPony(entity);
        float scale = (entity.isBaby() ? SizePreset.FOAL : pony.size()).scaleFactor();
        return DebugBoundingBoxRenderer.applyScale(scale, entity.getBoundingBox());
    }

    public void completeStateUpdate(PlayerEntityRenderState state) {
        if (state instanceof PreviewRenderState previewer) {
            previewer.completeStateUpdate(modelsLookup.apply(previewer.getRace()));
        }
    }

    public void updateState(T entity, S state, ModelAttributes.Mode mode, ItemModelManager resolver) {
        state.entityType = entity.getType();
        Pony pony = context.getEntityPony(entity);
        state.updateState(resolver, entity, modelsLookup.apply(pony.race()), pony, mode);
    }

    public void setupTransforms(S state, MatrixStack stack, float animationProgress, float bodyYaw) {
        float s = state.attributes.size.scaleFactor();
        stack.scale(s, s, s);

        if (state instanceof PlayerEntityRenderState && state.attributes.isSitting) {
            stack.translate(0, 0.125D, 0);
        }

        transformer.setupTransforms(state, stack, animationProgress, bodyYaw);

        if (RenderPass.getCurrent() == RenderPass.WORLD) {
            PonyPosture.of(state.attributes).transform(state, stack);
        }
    }

    public interface Transformer<S extends PonyRenderState> {
        void setupTransforms(S state, MatrixStack stack, float animationProgress, float bodyYaw);
    }

    public interface RegistrationHandler {
        SyncedPony getSyncedPony();
    }

    public interface ModelHolder<S extends PonyRenderState, M extends EntityModel<S> & PonyModel<S>> {
        void setModel(M model);
    }

    public static class SyncedPony {
        private Optional<Pony> lastRenderedPony = Optional.empty();
        private Supplier<Optional<PonyData>> lastPonyData = PonyDataLoader.NULL;
        private Optional<Pony> lastTransmittedPony = Optional.empty();
        private boolean seated;

        private final PlayerLikeEntity player;

        public SyncedPony(PlayerLikeEntity player) {
            this.player = player;
        }

        public Optional<Pony> getCachedPony() {
            return lastRenderedPony;
        }

        public PonyData getCachedPonyData() {
            return lastPonyData.get().orElse(PonyData.NULL);
        }

        public EntityDimensions modifyEyeHeight(EntityDimensions dimensions, EntityPose pose) {
            Pony pony = lastRenderedPony.orElse(null);
            float factor = pony == null || pony.race().isHuman() ? 1 : pony.size().eyeHeightFactor();
            if (factor == 1) {
                return dimensions;
            }
            float eyeHeight = dimensions.eyeHeight() * factor;
            if (player.hasVehicle()) {
                eyeHeight += player.getVehicleAttachmentPos(player.getVehicle()).getY();
            }

            return dimensions.withEyeHeight(eyeHeight);
        }

        public void synchronize() {
            Pony pony = Pony.getManager().getPony(player);
            boolean changed = pony.compareTo(lastRenderedPony.orElse(null)) != 0;
            boolean seated = player.hasVehicle();

            if (changed || seated != this.seated) {
                lastRenderedPony = Optional.of(pony);
                lastPonyData = pony.metadataGetter();
                player.calculateDimensions();
            }
            this.seated = seated;

            @Nullable
            PlayerEntity clientPlayer = MinecraftClient.getInstance().player;

            if (ClientChannel.isRegistered() && pony.compareTo(lastTransmittedPony.orElse(null)) != 0) {
                if (clientPlayer != null && (Objects.equals(player, clientPlayer) || Objects.equals(player.getUuid(), clientPlayer.getGameProfile().id()))) {
                    if (ClientChannel.broadcastPonyData(pony.metadata())) {
                        lastTransmittedPony = Optional.of(pony);
                    }
                }
            }

            if (changed) {
                PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, pony.metadata(), EnvType.CLIENT);
            }
        }
    }
}
