package com.minelittlepony.client.render;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.PonyRenderManager;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.transform.PonyPosture;
import com.minelittlepony.model.IPonyModel;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.util.math.MathUtil;
import com.mojang.blaze3d.platform.GlStateManager;

import javax.annotation.Nonnull;

import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class RenderPony<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> {

    public ModelWrapper<T, M> playerModel;

    private M ponyModel;

    private IPony pony;

    private final IPonyRender<T, M> renderer;

    private final FrustrumCheck<T> frustrum = new FrustrumCheck<>(this);

    public static void enableModelRenderProfile() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
    }

    public static void disableModelRenderProfile() {
        GlStateManager.disableBlend();
    }

    public RenderPony(IPonyRender<T, M> renderer) {
        this.renderer = renderer;
    }

    public VisibleRegion getFrustrum(T entity, VisibleRegion vanilla) {
        if (entity.isSleeping() || !MineLittlePony.getInstance().getConfig().frustrum) {
            return vanilla;
        }
        return frustrum.withCamera(entity, vanilla);
    }

    public void preRenderCallback(T entity, float ticks) {
        updateModel(entity);

        ponyModel.updateLivingState(entity, pony);

        float s = getScaleFactor();
        GlStateManager.scalef(s, s, s);
        enableModelRenderProfile();

        translateRider(entity, ticks);
    }

    public float getRenderYaw(T entity, float rotationYaw, float partialTicks) {
        if (entity.hasVehicle()) {
            Entity mount = entity.getVehicle();
            if (mount  instanceof LivingEntity) {
                return MathUtil.interpolateDegress(((LivingEntity)mount).field_6220, ((LivingEntity)mount).field_6283, partialTicks);
            }
        }

        return rotationYaw;
    }

    protected void translateRider(LivingEntity entity, float ticks) {
        if (entity.hasVehicle()) {
            Entity ridingEntity = entity.getVehicle();

            if (ridingEntity instanceof LivingEntity) {
                IPonyRender<LivingEntity, ?> renderer = PonyRenderManager.getInstance().getPonyRenderer((LivingEntity)ridingEntity);

                if (renderer != null) {
                    // negate vanilla translations so the rider begins at the ridees feet.
                    GlStateManager.translatef(0, -ridingEntity.getHeight(), 0);

                    @SuppressWarnings("unchecked")
                    IPony riderPony = renderer.getEntityPony((LivingEntity)ridingEntity);

                    renderer.translateRider((LivingEntity)ridingEntity, riderPony, entity, pony, ticks);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void applyPostureTransform(T player, float yaw, float ticks) {
        ((PonyPosture<T>)getPosture(player)).apply(player, ponyModel, yaw, ticks, 1);
    }

    @SuppressWarnings("unchecked")
    public void applyPostureRiding(T player, float yaw, float ticks) {
        ((PonyPosture<T>)getPosture(player)).apply(player, ponyModel, yaw, ticks, -1);
    }

    @Nonnull
    private PonyPosture<?> getPosture(T entity) {
        if (entity.isFallFlying()) {
            return PonyPosture.ELYTRA;
        }

        if (entity.isAlive() && entity.isSleeping()) {
            return PonyPosture.DEFAULT;
        }

        if (getModel().isSwimming()) {
            return PonyPosture.SWIMMING;
        }

        if (getModel().isGoingFast()) {
            return PonyPosture.FLIGHT;
        }

        return PonyPosture.FALLING;
    }

    public M getModel() {
        return playerModel.getBody();
    }

    public M setPonyModel(ModelWrapper<T, M> model) {
        playerModel = model;
        ponyModel = playerModel.getBody();

        return ponyModel;
    }

    public void updateModel(T entity) {
        pony = renderer.getEntityPony(entity);
        playerModel.apply(pony.getMetadata());
    }

    public IPony getPony(T entity) {
        updateModel(entity);
        return pony;
    }

    public float getShadowScale() {
        return getModel().getSize().getShadowSize();
    }

    public float getScaleFactor() {
        return getModel().getSize().getScaleFactor();
    }

    public double getNamePlateYOffset(T entity, double initial) {

        // We start by negating the height calculation done by mohjong.
        float y = -(entity.getHeight() + 0.5F - (entity.isSneaking() ? 0.25F : 0));

        // Then we add our own offsets.
        y += ponyModel.getModelHeight() * getScaleFactor() + 0.25F;

        if (entity.isSneaking()) {
            y -= 0.25F;
        }

        if (entity.hasVehicle()) {
            y += entity.getVehicle().getEyeHeight(entity.getPose());
        }

        if (entity.isSleeping()) {
            y /= 2;
        }

        return initial + y;
    }
}
