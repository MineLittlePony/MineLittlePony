package com.minelittlepony.render;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.render.layer.LayerHeldPonyItem;
import com.minelittlepony.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.render.layer.LayerPonyArmor;
import com.minelittlepony.render.layer.LayerPonyCustomHead;
import com.minelittlepony.render.layer.LayerPonyElytra;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class RenderPonyMob<T extends EntityLiving> extends RenderLiving<T> implements IRenderPony {

    protected ModelWrapper playerModel;

    public RenderPonyMob(RenderManager manager, ModelWrapper model) {
        super(manager, model.getModel(), 0.5F);
        playerModel = model;

        addLayers();
    }

    protected void addLayers() {
        addLayer(new LayerPonyArmor<>(this));
        addLayer(new LayerArrow(this));
        addLayer(new LayerPonyCustomHead<>(this));
        addLayer(new LayerPonyElytra<>(this));
        addLayer(createItemHoldingLayer());
    }

    protected LayerHeldPonyItem<T> createItemHoldingLayer() {
        return new LayerHeldPonyItemMagical<>(this);
    }

    @Override
    public void doRender(T entity, double xPosition, double yPosition, double zPosition, float yaw, float ticks) {
        if (entity.isSneaking()) {
            yPosition -= 0.125D;
        }
        super.doRender(entity, xPosition, yPosition, zPosition, yaw, ticks);
    }

    @Override
    protected void preRenderCallback(T entity, float ticks) {
        playerModel.getModel().isSneak = entity.isSneaking();
        playerModel.getModel().isFlying = !entity.onGround || entity.isElytraFlying();
        playerModel.getModel().isSleeping = false;

        ResourceLocation loc = getEntityTexture(entity);
        playerModel.apply(MineLittlePony.getInstance().getManager().getPony(loc, false).getMetadata());

        shadowSize = getShadowScale();

        float s = getScaleFactor();
        GlStateManager.scale(s, s, s);

        GlStateManager.translate(0, 0, -entity.width / 2); // move us to the center of the shadow
    }

    @Override
    public float getShadowScale() {
        if (mainModel.isChild) {
            return 0.25F;
        } else if (MineLittlePony.getConfig().showscale) {
            return 0.4F;
        }
        return 0.5F;
    }

    @Override
    public float getScaleFactor() {
        if (MineLittlePony.getConfig().showscale) return 0.9F;
        return 1;
    }

    @Override
    public ModelWrapper getPlayerModel() {
        return playerModel;
    }

    @Override
    @Nonnull
    protected final ResourceLocation getEntityTexture(T entity) {
        return HDSkinManager.INSTANCE.getConvertedSkin(getTexture(entity));
    }

    protected abstract ResourceLocation getTexture(T entity);
}
