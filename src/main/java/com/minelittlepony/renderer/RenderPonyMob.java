package com.minelittlepony.renderer;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyGender;
import com.minelittlepony.PonyRace;
import com.minelittlepony.TailLengths;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.minelittlepony.renderer.layer.LayerPonyArmor;
import com.minelittlepony.renderer.layer.LayerPonySkull;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public abstract class RenderPonyMob<T extends EntityLiving> extends RenderLiving<T> implements IRenderPony {

    protected PlayerModel playerModel;

    public RenderPonyMob(RenderManager renderManager, PlayerModel playerModel) {
        super(renderManager, playerModel.getModel(), 0.5F);
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
    }

    @Override
    protected void preRenderCallback(T entity, float partialTickTime) {
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

    protected ResourceLocation getTexture(ResourceLocation res) {
        return HDSkinManager.INSTANCE.getConvertedSkin(res);
    }
}
