package com.minelittlepony.client.render.entities.player;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.ducks.IRenderPony;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.client.render.layer.LayerDJPon3Head;
import com.minelittlepony.client.render.layer.LayerEntityOnPonyShoulder;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerPonyArmor;
import com.minelittlepony.client.render.layer.LayerPonyCape;
import com.minelittlepony.client.render.layer.LayerPonyCustomHead;
import com.minelittlepony.client.render.layer.LayerPonyElytra;
import com.minelittlepony.pony.IPony;

import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
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

        if (player.getRidingEntity() == null && !player.isPlayerSleeping()) {
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

        if (player.getRidingEntity() != null) {
            GlStateManager.translated(0, player.getYOffset(), 0);
        }
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double xPosition, double yPosition, double zPosition, float yaw, float ticks) {
        super.doRender(entity, xPosition, yPosition, zPosition, yaw, ticks);

        DebugBoundingBoxRenderer.instance.render(renderPony.getPony(entity), entity, ticks);
    }

    @Override
    public boolean shouldRender(AbstractClientPlayer entity, ICamera camera, double camX, double camY, double camZ) {
        if (entity.isPlayerSleeping() && entity == Minecraft.getInstance().player) {
            return true;
        }
        return super.shouldRender(entity, renderPony.getFrustrum(entity, camera), camX, camY, camZ);
    }

    @Override
    protected void renderLivingLabel(AbstractClientPlayer entity, String name, double x, double y, double z, int maxDistance) {
        if (entity.isPlayerSleeping()) {
            if (entity.bedLocation != null && entity.getEntityWorld().getBlockState(entity.bedLocation).getBlock() instanceof BlockBed) {
                double bedRad = Math.toRadians(entity.getBedOrientationInDegrees());
                x += Math.cos(bedRad);
                z -= Math.sin(bedRad);
            }
        }
        super.renderLivingLabel(entity, name, x, renderPony.getNamePlateYOffset(entity, y), z, maxDistance);
    }

    @Override
    public void doRenderShadowAndFire(Entity player, double x, double y, double z, float yaw, float ticks) {
        if (player.getRidingEntity() != null && ((AbstractClientPlayer)player).isPlayerSleeping()) {
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
        GlStateManager.translatef(side == EnumHandSide.LEFT ? 0.35F : -0.35F, -0.6F, 0);
        GlStateManager.rotatef(side == EnumHandSide.LEFT ? -90 : 90, 0, 1, 0);

        if (side == EnumHandSide.LEFT) {
            super.renderLeftArm(player);
        } else {
            super.renderRightArm(player);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void applyRotations(AbstractClientPlayer player, float age, float yaw, float ticks) {
        yaw = renderPony.getRenderYaw(player, yaw, ticks);
        super.applyRotations(player, age, yaw, ticks);

        renderPony.applyPostureTransform(player, yaw, ticks);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
        return renderPony.getPony(player).getTexture();
    }

    @Override
    public ResourceLocation getTexture(AbstractClientPlayer entity) {
        return getEntityTexture(entity);
    }

    @Override
    public ModelWrapper getModelWrapper() {
        return renderPony.playerModel;
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayer player) {
        return MineLittlePony.getInstance().getManager().getPony(player);
    }

    @Override
    public RenderPony<AbstractClientPlayer> getInternalRenderer() {
        return renderPony;
    }

}
