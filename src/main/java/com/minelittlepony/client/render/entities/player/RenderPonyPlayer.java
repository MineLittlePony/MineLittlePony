package com.minelittlepony.client.render.entities.player;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRender;
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
import com.mojang.blaze3d.platform.GlStateManager;

import java.util.List;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class RenderPonyPlayer extends PlayerEntityRenderer implements IPonyRender<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> {

    protected final RenderPony<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> renderPony = new RenderPony<>(this);

    public RenderPonyPlayer(EntityRenderDispatcher manager, boolean useSmallArms, ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> model) {
        super(manager, useSmallArms);

        this.model = renderPony.setPonyModel(model);

        addLayers();
    }

    protected void addLayers() {
        features.clear();

        addLayer(new LayerDJPon3Head<>(this));
        addLayer(new LayerPonyArmor<>(this));
        addFeature(new StuckArrowsFeatureRenderer<>(this));
        addLayer(new LayerPonyCustomHead<>(this));
        addLayer(new LayerPonyElytra<>(this));
        addLayer(new LayerHeldPonyItemMagical<>(this));
        addLayer(new LayerPonyCape<>(this));
        addLayer(new LayerEntityOnPonyShoulder<>(renderManager, this));
        addLayer(new LayerGear<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected boolean addLayer(FeatureRenderer<AbstractClientPlayerEntity, ? extends ClientPonyModel<AbstractClientPlayerEntity>> feature) {
        return ((List)features).add(feature);
    }

    @Override
    public float scaleAndTranslate(AbstractClientPlayerEntity player, float ticks) {
        if (!player.hasVehicle() && !player.isSleeping()) {
            float x = player.getWidth() / 2 * renderPony.getPony(player).getMetadata().getSize().getScaleFactor();
            float y = 0;

            if (player.isInSneakingPose()) {
                // Sneaking makes the player 1/15th shorter.
                // This should be compatible with height-changing mods.
                y += player.getHeight() / 15;
            }

            super.postRender(player, 0, y, x, 0, ticks);
        }

        return super.scaleAndTranslate(player, ticks);
    }

    @Override
    protected void scale(AbstractClientPlayerEntity player, float ticks) {
        renderPony.preRenderCallback(player, ticks);
        field_4673 = renderPony.getShadowScale();

        if (player.hasVehicle()) {
            GlStateManager.translated(0, player.getHeightOffset(), 0);
        }
    }

    @Override
    public void render(AbstractClientPlayerEntity entity, double xPosition, double yPosition, double zPosition, float yaw, float ticks) {
        super.render(entity, xPosition, yPosition, zPosition, yaw, ticks);

        DebugBoundingBoxRenderer.instance.render(renderPony.getPony(entity), entity, ticks);
    }

    @Override
    public boolean isVisible(AbstractClientPlayerEntity entity, VisibleRegion camera, double camX, double camY, double camZ) {
        if (entity.isSleeping() && entity == MinecraftClient.getInstance().player) {
            return true;
        }
        return super.isVisible(entity, renderPony.getFrustrum(entity, camera), camX, camY, camZ);
    }

    @Override
    protected void renderLabel(AbstractClientPlayerEntity entity, String name, double x, double y, double z, int maxDistance) {
        if (entity.isSleeping()) {
            if (entity.getSleepingPosition().isPresent() && entity.getEntityWorld().getBlockState(entity.getSleepingPosition().get()).getBlock() instanceof BedBlock) {
                double bedRad = Math.toRadians(entity.getSleepingDirection().asRotation());
                x += Math.cos(bedRad);
                z -= Math.sin(bedRad);
            }
        }
        super.renderLabel(entity, name, x, renderPony.getNamePlateYOffset(entity, y), z, maxDistance);
    }

    @Override
    public void postRender(Entity player, double x, double y, double z, float yaw, float ticks) {
        if (player.hasVehicle() && ((LivingEntity)player).isSleeping()) {
            super.postRender(player, x, y, z, yaw, ticks);
        }
    }

    @Override
    public final void renderRightArm(AbstractClientPlayerEntity player) {
        renderArm(player, Arm.RIGHT);
    }

    @Override
    public final void renderLeftArm(AbstractClientPlayerEntity player) {
        renderArm(player, Arm.LEFT);
    }

    protected void renderArm(AbstractClientPlayerEntity player, Arm side) {
        renderPony.updateModel(player);
        bindEntityTexture(player);

        GlStateManager.pushMatrix();
        float reflect = side == Arm.LEFT ? 1 : -1;

        GlStateManager.translatef(reflect * -0.1F, -0.74F, 0);

        if (side == Arm.LEFT) {
            super.renderLeftArm(player);
        } else {
            super.renderRightArm(player);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity player, float age, float yaw, float ticks) {
        yaw = renderPony.getRenderYaw(player, yaw, ticks);
        super.setupTransforms(player, age, yaw, ticks);

        renderPony.applyPostureTransform(player, yaw, ticks);
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        return renderPony.getPony(player).getTexture();
    }

    @Override
    public ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getModelWrapper() {
        return renderPony.playerModel;
    }

    @Override
    public RenderPony<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getInternalRenderer() {
        return renderPony;
    }

    @Override
    public Identifier findTexture(AbstractClientPlayerEntity entity) {
        return getTexture(entity);
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayerEntity entity) {
        return MineLittlePony.getInstance().getManager().getPony(entity);
    }

}
