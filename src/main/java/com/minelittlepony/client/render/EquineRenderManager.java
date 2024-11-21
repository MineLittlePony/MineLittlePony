package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.ClientChannel;
import com.minelittlepony.api.events.PonyDataCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.client.PonyDataLoader;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.transform.PonyPosture;
import com.minelittlepony.mson.api.ModelKey;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import org.jetbrains.annotations.Nullable;

public class EquineRenderManager<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>> {

    private Models<M> models;

    private Function<S, Models<M>> modelsLookup = s -> models;

    private final PonyRenderContext<T, S, M> context;
    private final Transformer<? super S> transformer;

    private final FrustrumCheck<S> frustrum = new FrustrumCheck<>();

    private final MinecraftClient client = MinecraftClient.getInstance();

    public static void disableModelRenderProfile() {
        RenderSystem.disableBlend();
    }

    public EquineRenderManager(PonyRenderContext<T, S, M> context, Transformer<? super S> transformer, Models<M> models) {
        this.context = context;
        this.transformer = transformer;
        this.models = models;
        context.setModel(models.body());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public EquineRenderManager(PonyRenderContext<T, S, M> context, Transformer<? super S> transformer, ModelKey<? super M> key) {
        this(context, transformer, new Models(key));
    }

    public void setModelsLookup(Function<S, Models<M>> modelsLookup) {
        this.modelsLookup = modelsLookup;
    }

    public Models<M> getModels() {
        return models;
    }

    public Frustum getFrustrum(T entity, Frustum vanilla) {
        if (RenderPass.getCurrent() == RenderPass.HUD) {
            return FrustrumCheck.ALWAYS_VISIBLE;
        }

        if (entity.isSleeping() || !PonyConfig.getInstance().frustrum.get()) {
            return vanilla;
        }

        return frustrum.withCamera(context.getVanillaRenderer().getAndUpdateRenderState(entity, client.getRenderTickCounter().getTickDelta(false)), vanilla);
    }

    public void updateState(T entity, S state, ModelAttributes.Mode mode) {
        models = modelsLookup.apply(state);
        context.setModel(models.body());
        state.updateState(entity, models.body(), context.getEntityPony(entity), mode);
    }

    public void setupTransforms(S state, MatrixStack stack, float animationProgress, float bodyYaw) {
        float s = state.size.scaleFactor();
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
        @Nullable
        private Pony lastRenderedPony;
        private Supplier<Optional<PonyData>> lastPonyData = PonyDataLoader.NULL;
        @Nullable
        private Pony lastTransmittedPony;

        public Pony getCachedPony() {
            return lastRenderedPony;
        }

        public PonyData getCachedPonyData() {
            return lastPonyData.get().orElse(PonyData.NULL);
        }

        public void synchronize(PlayerEntity player) {
            Pony pony = Pony.getManager().getPony(player);
            boolean changed = pony.compareTo(lastRenderedPony) != 0;

            if (changed) {
                lastRenderedPony = pony;
                lastPonyData = pony.metadataGetter();
                player.calculateDimensions();
            }

            if (!(player instanceof PreviewModel)) {
                @Nullable
                PlayerEntity clientPlayer = MinecraftClient.getInstance().player;

                if (ClientChannel.isRegistered() && pony.compareTo(lastTransmittedPony) != 0) {
                    if (clientPlayer != null && (Objects.equals(player, clientPlayer) || Objects.equals(player.getGameProfile(), clientPlayer.getGameProfile()))) {
                        if (ClientChannel.broadcastPonyData(pony.metadata())) {
                            lastTransmittedPony = pony;
                        }
                    }
                }

                if (changed) {
                    PonyDataCallback.EVENT.invoker().onPonyDataAvailable(player, pony.metadata(), EnvType.CLIENT);
                }
            }
        }
    }
}
