package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.ClientChannel;
import com.minelittlepony.api.events.PonyDataCallback;
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
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public class EquineRenderManager<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>> {

    private Models<M> models;

    private Function<Race, Models<M>> modelsLookup = race -> models;

    private final PonyRenderContext<T, S, M> context;
    private final Transformer<? super S> transformer;

    public static void disableModelRenderProfile() {
        RenderSystem.disableBlend();
    }

    public EquineRenderManager(PonyRenderContext<T, S, M> context, Transformer<? super S> transformer, Function<Race, Models<M>> modelsLookup) {
        this.context = context;
        this.transformer = transformer;
        setModelsLookup(modelsLookup);
        this.models = this.modelsLookup.apply(Race.EARTH);
        context.setModel(models.body());
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

    public void setModelsLookup(Function<Race, Models<M>> modelsLookup) {
        this.modelsLookup = Util.memoize(modelsLookup);
    }

    public Models<M> getModels() {
        return models;
    }

    public Box getBoundingBox(T entity, Box box) {

        if (entity.isSleeping() || !PonyConfig.getInstance().frustrum.get()) {
            return box;
        }

        Pony pony = context.getEntityPony(entity);
        float scale = (entity.isBaby() ? SizePreset.FOAL : pony.size()).scaleFactor();
        return DebugBoundingBoxRenderer.applyScale(scale, box);
    }

    public void updateState(T entity, S state, ModelAttributes.Mode mode, ItemModelManager modelManager) {
        Pony pony = context.getEntityPony(entity);
        models = modelsLookup.apply(pony.race());
        context.setModel(models.body());
        state.updateState(entity, models.body(), pony, mode);
        if (PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow()) {
            modelManager.updateForLivingEntity(
                state.glintlessRightHandItemState, getWithoutGlint(entity.getStackInArm(Arm.RIGHT)), ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, false, entity
            );
            modelManager.updateForLivingEntity(
                state.glintlessLeftHandItemState, getWithoutGlint(entity.getStackInArm(Arm.LEFT)), ModelTransformationMode.THIRD_PERSON_LEFT_HAND, true, entity
            );
        } else {
            state.glintlessRightHandItemState.clear();
            state.glintlessLeftHandItemState.clear();
        }
    }

    private static ItemStack getWithoutGlint(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
        }
        return stack;
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

        public Optional<Pony> getCachedPony() {
            return lastRenderedPony;
        }

        public PonyData getCachedPonyData() {
            return lastPonyData.get().orElse(PonyData.NULL);
        }

        public EntityDimensions modifyEyeHeight(PlayerEntity player, EntityDimensions dimensions, EntityPose pose) {
            Pony pony = lastRenderedPony.orElse(null);
            float factor = pony == null || pony.race().isHuman() ? 1 : pony.size().eyeHeightFactor();
            if (factor == 1) {
                return dimensions;
            }
            float eyeHeight = dimensions.eyeHeight() * factor;
            if (player.hasVehicle()) {
                Vec3d attachment = dimensions.attachments().getPointNullable(EntityAttachmentType.VEHICLE, 0, 0);
                if (attachment != null) {
                    double yAttachment = attachment.getY();
                    eyeHeight += yAttachment * factor;
                }
            }

            return dimensions.withEyeHeight(eyeHeight);
        }

        public void synchronize(PlayerEntity player) {
            Pony pony = Pony.getManager().getPony(player);
            boolean changed = pony.compareTo(lastRenderedPony.orElse(null)) != 0;
            boolean seated = player.hasVehicle();

            if (changed || seated != this.seated) {
                lastRenderedPony = Optional.of(pony);
                lastPonyData = pony.metadataGetter();
                player.calculateDimensions();
            }
            this.seated = seated;

            if (!(player instanceof PreviewModel)) {
                @Nullable
                PlayerEntity clientPlayer = MinecraftClient.getInstance().player;

                if (ClientChannel.isRegistered() && pony.compareTo(lastTransmittedPony.orElse(null)) != 0) {
                    if (clientPlayer != null && (Objects.equals(player, clientPlayer) || Objects.equals(player.getGameProfile(), clientPlayer.getGameProfile()))) {
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
}
