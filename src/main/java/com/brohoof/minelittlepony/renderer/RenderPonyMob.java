package com.brohoof.minelittlepony.renderer;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.TailLengths;
import com.brohoof.minelittlepony.ducks.IRenderPony;
import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyArmor;
import com.brohoof.minelittlepony.renderer.layer.LayerPonySkull;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public abstract class RenderPonyMob<T extends EntityLiving> extends RenderLiving<T> implements IRenderPony {

    protected AbstractPonyModel mobModel;
    protected PlayerModel playerModel;

    public RenderPonyMob(RenderManager renderManager, PlayerModel playerModel) {
        super(renderManager, playerModel.getModel(), playerModel.getShadowsize());
        this.mobModel = playerModel.getModel();
        this.playerModel = playerModel;

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        // this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonySkull(this));
    }

    @Override
    public void doRender(T entity, double xPosition, double yPosition, double zPosition, float yaw,
            float partialTicks) {
        double yOrigin = yPosition;
        if (entity.isSneaking()) {
            yOrigin -= 0.125D;
        }
        super.doRender(entity, xPosition, yOrigin, zPosition, yaw, partialTicks);
        this.playerModel.getModel().aimedBow = false;
        this.playerModel.getModel().isSneak = false;
        this.playerModel.getModel().heldItemRight = 0;
    }

    @Override
    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {

        ItemStack heldItem = entitylivingbaseIn.getHeldItem();
        this.playerModel.getModel().heldItemRight = heldItem == null ? 0 : 1;

        this.playerModel.getModel().isSneak = false;
        this.playerModel.getModel().isFlying = false;
        this.playerModel.getModel().isSleeping = false;

        this.playerModel.getModel().metadata.setRace(PonyRace.EARTH);
        this.playerModel.getModel().metadata.setGender(PonyGender.MARE);
        this.playerModel.getModel().metadata.setTail(TailLengths.FULL);

        if (MineLittlePony.getConfig().showscale) {
            this.shadowSize = 0.4F;
        }
    }

    @Override
    public PlayerModel getPony() {
        return playerModel;
    }
}
