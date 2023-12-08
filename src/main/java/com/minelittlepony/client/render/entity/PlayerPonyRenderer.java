package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.SkinsProxy;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.util.render.RenderLayerUtil;

import java.util.*;
import java.util.function.Function;

import com.minelittlepony.client.render.EquineRenderManager;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.*;

public class PlayerPonyRenderer extends PlayerEntityRenderer implements PonyRenderContext<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> {
    private final Function<Race, Models<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>>> modelsCache;
    protected final EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager;

    public PlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim);
        modelsCache = Util.memoize(race -> ModelType.getPlayerModel(race).create(slim));
        manager = new EquineRenderManager<>(this, super::setupTransforms, modelsCache.apply(Race.EARTH));
        manager.setModelsLookup(entity -> modelsCache.apply(getPlayerRace(entity, getEntityPony(entity))));
        addLayers(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void addLayers(EntityRendererFactory.Context context) {
        // remove vanilla features (keep modded ones)
        features.removeIf(feature -> {
            return feature instanceof ArmorFeatureRenderer
                    || feature instanceof PlayerHeldItemFeatureRenderer
                    || feature instanceof Deadmau5FeatureRenderer
                    || feature instanceof CapeFeatureRenderer
                    || feature instanceof HeadFeatureRenderer
                    || feature instanceof ElytraFeatureRenderer
                    || feature instanceof ShoulderParrotFeatureRenderer;
        });
        addLayer(new ArmourFeature<>(this, context.getModelManager()));
        addLayer(new HeldItemFeature(this, context.getHeldItemRenderer()));
        addLayer(new DJPon3Feature<>(this));
        addLayer(new CapeFeature<>(this));
        addLayer(new SkullFeature<>(this, context.getModelLoader()));
        addLayer(new ElytraFeature<>(this));
        addLayer(new PassengerFeature<>(this, context));
        addLayer(new GearFeature<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected boolean addLayer(FeatureRenderer<AbstractClientPlayerEntity, ? extends ClientPonyModel<AbstractClientPlayerEntity>> feature) {
        return ((List)features).add(feature);
    }

    @Override
    protected void scale(AbstractClientPlayerEntity entity, MatrixStack stack, float tickDelta) {
        if (manager.getModels().body().getAttributes().isSitting && entity.hasVehicle()) {
            stack.translate(0, entity.getRidingOffset(entity.getVehicle()), 0);
        }
    }

    @Override
    public void render(AbstractClientPlayerEntity entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        // EntityModelFeatures: We have to force it to use our models otherwise EMF overrides it and breaks pony rendering
        manager.preRender(entity, ModelAttributes.Mode.THIRD_PERSON);
        shadowRadius = manager.getShadowSize();
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
        DebugBoundingBoxRenderer.render(getEntityPony(entity), this, entity, stack, renderContext, tickDelta);

        // Translate the shadow position after everything is done
        // (shadows are drawn after us)
        /*
        if (!entity.hasVehicle() && !entity.isSleeping()) {
            float yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
            float l = entity.getWidth() / 2 * manager.getScaleFactor();

            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(yaw));
            stack.translate(0, 0, -l);
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        }
        */

    }

    protected Race getPlayerRace(AbstractClientPlayerEntity entity, Pony pony) {
        return pony.race();
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        manager.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public boolean shouldRender(AbstractClientPlayerEntity entity, Frustum camera, double camX, double camY, double camZ) {
        if (entity.isSleeping() && entity == MinecraftClient.getInstance().player) {
            return !MinecraftClient.getInstance().options.getPerspective().isFirstPerson()
                    && super.shouldRender(entity, camera, camX, camY, camZ);
        }
        return super.shouldRender(entity, manager.getFrustrum(entity, camera), camX, camY, camZ);
    }

    @Override
    protected void renderLabelIfPresent(AbstractClientPlayerEntity entity, Text name, MatrixStack stack, VertexConsumerProvider renderContext, int maxDistance) {
        stack.push();

        if (entity.isSleeping()) {
            if (entity.getSleepingPosition().isPresent() && entity.getEntityWorld().getBlockState(entity.getSleepingPosition().get()).getBlock() instanceof BedBlock) {
                double bedRad = Math.toRadians(entity.getSleepingDirection().asRotation());

                stack.translate(Math.cos(bedRad), 0, -Math.sin(bedRad));
            }
        }
        stack.translate(0, manager.getNamePlateYOffset(entity), 0);
        super.renderLabelIfPresent(entity, name, stack, renderContext, maxDistance);
        stack.pop();
    }

    @Override
    public final void renderRightArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player) {
        renderArm(stack, renderContext, lightUv, player, Arm.RIGHT);
    }

    @Override
    public final void renderLeftArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player) {
        renderArm(stack, renderContext, lightUv, player, Arm.LEFT);
    }

    protected void renderArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player, Arm side) {
        manager.preRender(player, ModelAttributes.Mode.FIRST_PERSON);

        stack.push();
        float reflect = side == Arm.LEFT ? 1 : -1;

        stack.translate(reflect * 0.1F, -0.54F, 0);

        Identifier texture = getTexture(player);
        Identifier playerSkin = player.getSkinTextures().texture();
        VertexConsumerProvider interceptedContext = layer -> {
            return renderContext.getBuffer(RenderLayerUtil
                    .getTexture(layer)
                    .filter(playerSkin::equals)
                    .map(i -> RenderLayer.getEntityTranslucent(texture))
                    .orElse(layer)
            );
        };

        if (side == Arm.LEFT) {
            super.renderLeftArm(stack, interceptedContext, lightUv, player);
        } else {
            super.renderRightArm(stack, interceptedContext, lightUv, player);
        }

        stack.pop();
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        return getEntityPony(player).texture();
    }

    @Override
    public void setModel(ClientPonyModel<AbstractClientPlayerEntity> model) {
        this.model = model;
    }

    @Override
    public EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getInternalRenderer() {
        return manager;
    }

    @Override
    public Pony getEntityPony(AbstractClientPlayerEntity entity) {
        return Pony.getManager().getPony(entity);
    }

    @Override
    public Identifier getDefaultTexture(AbstractClientPlayerEntity entity, Wearable wearable) {
        return SkinsProxy.instance.getSkin(wearable.getId(), entity).orElseGet(() -> {
            if (wearable.isSaddlebags() && getInternalRenderer().getModels().body().getRace().supportsLegacySaddlebags()) {
                return getTexture(entity);
            }

            return wearable.getDefaultTexture();
        });
    }
}
