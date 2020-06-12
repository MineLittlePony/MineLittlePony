package com.minelittlepony.render;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.transform.PonyPosture;
import com.minelittlepony.util.math.MathUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class RenderPony<T extends EntityLivingBase> {

    public ModelWrapper playerModel;

    protected AbstractPonyModel ponyModel;

    private IPony pony;

    private IRenderPony<T> renderer;

    private FrustrumCheck<T> frustrum = new FrustrumCheck<>(this);

    public static void enableModelRenderProfile() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
    }

    public static void disableModelRenderProfile() {
        GlStateManager.disableBlend();
    }

    public RenderPony(IRenderPony<T> renderer) {
        this.renderer = renderer;
    }

    public ICamera getFrustrum(T entity, ICamera vanilla) {
        if (entity.isPlayerSleeping() || !MineLittlePony.getConfig().frustrum) {
            return vanilla;
        }
        return frustrum.withCamera(entity, vanilla);
    }

    public void preRenderCallback(T entity, float ticks) {
        updateModel(entity);

        float s = getScaleFactor();
        GlStateManager.scale(s, s, s);
        enableModelRenderProfile();

        translateRider(entity, ticks);
    }

    public float getRenderYaw(T entity, float rotationYaw, float partialTicks) {
        if (entity.isRiding()) {
            Entity mount = entity.getRidingEntity();
            if (mount  instanceof EntityLivingBase) {
                return MathUtil.interpolateDegress(((EntityLivingBase)mount).prevRenderYawOffset, ((EntityLivingBase)mount).renderYawOffset, partialTicks);
            }
        }

        return rotationYaw;
    }

    protected void translateRider(EntityLivingBase entity, float ticks) {
        if (entity.isRiding()) {
            Entity ridingEntity = entity.getRidingEntity();

            if (ridingEntity instanceof EntityLivingBase) {
                IRenderPony<EntityLivingBase> renderer = MineLittlePony.getInstance().getRenderManager().getPonyRenderer((EntityLivingBase)ridingEntity);

                if (renderer != null) {
                    // negate vanilla translations so the rider begins at the ridees feet.
                    GlStateManager.translate(0, -ridingEntity.height, 0);

                    IPony riderPony = renderer.getEntityPony((EntityLivingBase)ridingEntity);

                    renderer.translateRider((EntityLivingBase)ridingEntity, riderPony, entity, pony, ticks);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void applyPostureTransform(T player, float yaw, float ticks) {
        PonyPosture<?> posture = getPosture(player);
        if (posture != null && posture.applies(player)) {
            double motionX = player.posX - player.prevPosX;
            double motionY = player.onGround ? 0 : player.posY - player.prevPosY;
            double motionZ = player.posZ - player.prevPosZ;
            ((PonyPosture<EntityLivingBase>)posture).transform(ponyModel, player, motionX, motionY, motionZ, yaw, ticks);
        }
    }

    @SuppressWarnings("unchecked")
    public void applyPostureRiding(T player, float yaw, float ticks) {
        PonyPosture<?> posture = getPosture(player);
        if (posture != null && posture.applies(player)) {
            double motionX = player.posX - player.prevPosX;
            double motionY = player.onGround ? 0 : player.posY - player.prevPosY;
            double motionZ = player.posZ - player.prevPosZ;

            ((PonyPosture<EntityLivingBase>)posture).transform(ponyModel, player, motionX, -motionY, motionZ, yaw, ticks);
        }
    }

    private PonyPosture<?> getPosture(T entity) {
        if (entity.isElytraFlying()) {
            return PonyPosture.ELYTRA;
        }

        if (entity.isEntityAlive() && entity.isPlayerSleeping()) return null;

        if (ponyModel.isSwimming()) {
            return PonyPosture.SWIMMING;
        }

        if (ponyModel.isGoingFast()) {
            return PonyPosture.FLIGHT;
        }

        return PonyPosture.FALLING;
    }

    public AbstractPonyModel setPonyModel(ModelWrapper model) {
        playerModel = model;
        ponyModel = playerModel.getBody();

        return ponyModel;
    }

    public void updateModel(T entity) {
        pony = renderer.getEntityPony(entity);
        playerModel.apply(pony.getMetadata());

        ponyModel.updateLivingState(entity, pony);
    }

    public IPony getPony(T entity) {
        updateModel(entity);
        return pony;
    }

    public float getShadowScale() {
        return ponyModel.getSize().getShadowSize();
    }

    public float getScaleFactor() {
        return ponyModel.getSize().getScaleFactor();
    }

    public double getNamePlateYOffset(T entity, double initial) {

        // We start by negating the height calculation done by mohjong.
        float y = -(entity.height + 0.5F - (entity.isSneaking() ? 0.25F : 0));

        // Then we add our own offsets.
        y += ponyModel.getModelHeight() * getScaleFactor() + 0.25F;

        if (entity.isSneaking()) {
            y -= 0.25F;
        }

        if (entity.isRiding()) {
            y += entity.getRidingEntity().getEyeHeight();
        }

        if (entity.isPlayerSleeping()) {
            y /= 2;
        }

        return initial + y;
    }
}
