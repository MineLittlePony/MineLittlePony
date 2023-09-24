package com.minelittlepony.client.render;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.RenderPass;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.api.pony.network.fabric.Channel;
import com.minelittlepony.api.pony.network.fabric.PonyDataCallback;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.transform.PonyPosture;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.util.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Objects;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EquineRenderManager<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> {

    private ModelWrapper<T, M> playerModel;

    private final IPonyRenderContext<T, M> renderer;

    private final FrustrumCheck<T> frustrum = new FrustrumCheck<>(this);

    public static void disableModelRenderProfile() {
        RenderSystem.disableBlend();
    }

    public EquineRenderManager(IPonyRenderContext<T, M> renderer) {
        this.renderer = renderer;
    }

    public IPonyRenderContext<T, M> getContext() {
        return renderer;
    }

    public ModelWrapper<T, M> getModelWrapper() {
        return playerModel;
    }

    public M getModel() {
        return playerModel.body();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ModelWrapper<T, M> setModel(ModelKey<? super M> key) {
        return setModel(new ModelWrapper(key));
    }

    public ModelWrapper<T, M> setModel(ModelWrapper<T, M> wrapper) {
        playerModel = wrapper;
        return wrapper;
    }

    public Frustum getFrustrum(T entity, Frustum vanilla) {
        if (RenderPass.getCurrent() == RenderPass.HUD) {
            return FrustrumCheck.ALWAYS_VISIBLE;
        }

        if (entity.isSleeping() || !MineLittlePony.getInstance().getConfig().frustrum.get()) {
            return vanilla;
        }
        return frustrum.withCamera(entity, vanilla);
    }

    public float getRenderYaw(T entity, float rotationYaw, float partialTicks) {
        if (entity.hasVehicle()) {
            Entity mount = entity.getVehicle();
            if (mount instanceof LivingEntity) {
                return MathUtil.interpolateDegress(((LivingEntity) mount).prevBodyYaw, ((LivingEntity) mount).bodyYaw, partialTicks);
            }
        }

        return rotationYaw;
    }

    public void preRenderCallback(T entity, MatrixStack stack, float ticks) {
        updateModel(entity, ModelAttributes.Mode.THIRD_PERSON);

        float s = getScaleFactor();
        stack.scale(s, s, s);

        translateRider(entity, stack, ticks);
    }

    private void translateRider(T entity, MatrixStack stack, float ticks) {
        if (entity.hasVehicle() && entity.getVehicle() instanceof LivingEntity) {

            LivingEntity ridingEntity = (LivingEntity) entity.getVehicle();
            IPonyRenderContext<LivingEntity, ?> renderer = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(ridingEntity);

            if (renderer != null) {
                // negate vanilla translations so the rider begins at the ridees feet.
                stack.translate(0, -ridingEntity.getHeight(), 0);

                IPony riderPony = renderer.getEntityPony(ridingEntity);

                renderer.translateRider(ridingEntity, riderPony, entity, renderer.getEntityPony(entity), stack, ticks);
            }
        }
    }

    public void setupTransforms(T entity, MatrixStack stack, float yaw, float tickDelta) {
        PonyPosture.of(getModel().getAttributes()).apply(entity, getModel(), stack, yaw, tickDelta, 1);
    }

    public void applyPostureRiding(T entity, MatrixStack stack, float yaw, float tickDelta) {
        PonyPosture.of(getModel().getAttributes()).apply(entity, getModel(), stack, yaw, tickDelta, -1);
    }

    public IPony updateModel(T entity, ModelAttributes.Mode mode) {
        IPony pony = renderer.getEntityPony(entity);
        playerModel.applyMetadata(pony.metadata());

        if (pony.hasMetadata() && entity instanceof RegistrationHandler && ((RegistrationHandler)entity).shouldUpdateRegistration(pony)) {
            entity.calculateDimensions();

            PlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer != null) {
                if (Objects.equals(entity, clientPlayer) || Objects.equals(((PlayerEntity)entity).getGameProfile(), clientPlayer.getGameProfile())) {
                    Channel.broadcastPonyData(new MsgPonyData(pony.metadata(), pony.defaulted()));
                }
            }
            PonyDataCallback.EVENT.invoker().onPonyDataAvailable((PlayerEntity)entity, pony.metadata(), pony.defaulted(), EnvType.CLIENT);
        }

        getModel().updateLivingState(entity, pony, mode);

        return pony;
    }

    public float getScaleFactor() {
        return getModel().getSize().getScaleFactor();
    }

    public double getNamePlateYOffset(T entity) {

        // We start by negating the height calculation done by mahjong.
        float y = -(entity.getHeight() + 0.5F);

        // Then we add our own offsets.
        y += getModel().getAttributes().visualHeight * getScaleFactor() + 0.25F;

        if (entity.isSneaking()) {
            y -= 0.25F;
        }

        if (entity.hasVehicle()) {
            y += entity.getVehicle().getEyeHeight(entity.getPose());
        }

        if (entity.isSleeping()) {
            y /= 2;
        }

        return y;
    }

    public interface RegistrationHandler {
        boolean shouldUpdateRegistration(IPony pony);
    }
}
