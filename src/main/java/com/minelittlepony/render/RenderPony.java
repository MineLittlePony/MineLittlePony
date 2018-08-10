package com.minelittlepony.render;

import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.transform.PonyPosture;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class RenderPony<T extends EntityLivingBase> {

    public ModelWrapper playerModel;

    protected AbstractPonyModel ponyModel;

    private Pony pony;

    private IRenderPony<T> renderer;

    public RenderPony(IRenderPony<T> renderer) {
        this.renderer = renderer;
    }

    public void preRenderCallback(T entity, float ticks) {
        updateModel(entity);

        ponyModel.updateLivingState(entity, pony);

        float s = getScaleFactor();
        GlStateManager.scale(s, s, s);
    }

    @SuppressWarnings("unchecked")
    public void applyPostureTransform(T player, float pitch, float yaw, float ticks) {
        PonyPosture<?> posture = getPosture(player);
        if (posture != null && posture.applies(player)) {
            double motionX = player.posX - player.prevPosX;
            double motionY = player.onGround ? 0 : player.posY - player.prevPosY;
            double motionZ = player.posZ - player.prevPosZ;
            ((PonyPosture<EntityLivingBase>)posture).transform(ponyModel, player, motionX, motionY, motionZ, pitch, yaw, ticks);
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
    }

    public Pony getPony(T entity) {
        updateModel(entity);
        return pony;
    }

    public float getShadowScale() {
        return ponyModel.getSize().getShadowSize();
    }

    public float getScaleFactor() {
        return ponyModel.getSize().getScaleFactor();
    }
}
