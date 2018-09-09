package com.minelittlepony.render.player;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.ducks.IRenderPony;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.render.RenderPony;
import com.minelittlepony.render.layer.LayerDJPon3Head;
import com.minelittlepony.render.layer.LayerEntityOnPonyShoulder;
import com.minelittlepony.render.layer.LayerGear;
import com.minelittlepony.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.render.layer.LayerPonyArmor;
import com.minelittlepony.render.layer.LayerPonyCape;
import com.minelittlepony.render.layer.LayerPonyCustomHead;
import com.minelittlepony.render.layer.LayerPonyElytra;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderPonyPlayer extends RenderPlayer implements IRenderPony<AbstractClientPlayer> {

    protected final RenderPony<AbstractClientPlayer> renderPony = new RenderPony<>(this);

    public RenderPonyPlayer(RenderManager manager, boolean useSmallArms, ModelWrapper model) {
        super(manager, useSmallArms);

        mainModel = renderPony.setPonyModel(model);

        addLayers();
    }

    protected void addLayers() {
        layerRenderers.clear();

        addLayer(new LayerDJPon3Head(this));
        addLayer(new LayerPonyArmor<>(this));
        addLayer(new LayerArrow(this));
        addLayer(new LayerPonyCustomHead<>(this));
        addLayer(new LayerPonyElytra<>(this));
        addLayer(new LayerHeldPonyItemMagical<>(this));
        addLayer(new LayerPonyCape(this));
        addLayer(new LayerEntityOnPonyShoulder(renderManager, this));
        addLayer(new LayerGear<>(this));
    }

    @Override
    public float prepareScale(AbstractClientPlayer player, float ticks) {

        if (!player.isRiding() && !player.isPlayerSleeping()) {
            float x = player.width/2;
            float y = 0;

            if (player.isSneaking()) {
                // Sneaking makes the player 1/15th shorter.
                // This should be compatible with height-changing mods.
                y += player.height / 15;
            }

            super.doRenderShadowAndFire(player, 0, y, x, 0, ticks);
        }

        return super.prepareScale(player, ticks);
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer player, float ticks) {
        renderPony.preRenderCallback(player, ticks);
        shadowSize = renderPony.getShadowScale();

        if (player.isRiding()) {
            GlStateManager.translate(0, player.getYOffset(), 0);
        }
    }

    @Override
    protected void renderLivingLabel(AbstractClientPlayer entity, String name, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entity, name, x, renderPony.getNamePlateYOffset(entity, y), z, maxDistance);
    }

    @Override
    public void doRenderShadowAndFire(Entity player, double x, double y, double z, float yaw, float ticks) {
        if (player.isRiding() && ((AbstractClientPlayer)player).isPlayerSleeping()) {
            super.doRenderShadowAndFire(player, x, y, z, yaw, ticks);
        }
    }

    @Override
    public final void renderRightArm(AbstractClientPlayer player) {
        renderArm(player, EnumHandSide.RIGHT);
    }

    @Override
    public final void renderLeftArm(AbstractClientPlayer player) {
        renderArm(player, EnumHandSide.LEFT);
    }

    protected void renderArm(AbstractClientPlayer player, EnumHandSide side) {
        renderPony.updateModel(player);
        bindEntityTexture(player);

        GlStateManager.pushMatrix();
        GlStateManager.translate(side == EnumHandSide.LEFT ? 0.35 : -0.35, -0.6, 0);
        GlStateManager.rotate(side == EnumHandSide.LEFT ? -90 : 90, 0, 1, 0);

        if (side == EnumHandSide.LEFT) {
            super.renderLeftArm(player);
        } else {
            super.renderRightArm(player);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void applyRotations(AbstractClientPlayer player, float yaw, float pitch, float ticks) {
        super.applyRotations(player, yaw, pitch, ticks);

        renderPony.applyPostureTransform(player, yaw, pitch, ticks);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        return renderPony.getPony(player).getTexture();
    }

    @Override
    public ModelWrapper getModelWrapper() {
        return renderPony.playerModel;
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayer player) {
        return MineLittlePony.getInstance().getManager().getPony(player);
    }

}
