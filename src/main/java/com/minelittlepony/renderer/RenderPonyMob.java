package com.minelittlepony.renderer;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.minelittlepony.renderer.layer.LayerPonyArmor;
import com.minelittlepony.renderer.layer.LayerPonyCustomHead;
import com.minelittlepony.renderer.layer.LayerPonyElytra;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class RenderPonyMob<T extends EntityLiving> extends RenderLiving<T> implements IRenderPony {

    protected PlayerModel playerModel;

    public RenderPonyMob(RenderManager renderManager, PlayerModel playerModel) {
        super(renderManager, playerModel.getModel(), 0.5F);
        this.playerModel = playerModel;

        addLayers();
    }

    protected void addLayers() {

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        // this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonyCustomHead(this));
        this.addLayer(new LayerPonyElytra(this));
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

        ResourceLocation loc = getEntityTexture(entity);
        this.playerModel.apply(MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry(loc).metadata);

        if (MineLittlePony.getConfig().showscale) {
            this.shadowSize = 0.4F;
        } else {
            this.shadowSize = 0.5F;
        }
    }

    @Override
    public PlayerModel getPony() {
        return playerModel;
    }

    @Override
    @Nonnull
    protected final ResourceLocation getEntityTexture(T entity) {
        return HDSkinManager.INSTANCE.getConvertedSkin(getTexture(entity));
    }

    protected abstract ResourceLocation getTexture(T entity);
}
