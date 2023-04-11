package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.EquineRenderManager;

import java.util.List;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class PlayerPonyRenderer extends PlayerEntityRenderer implements IPonyRenderContext<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> {

    protected final EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager = new EquineRenderManager<>(this);

    public PlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim, PlayerModelKey<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> key) {
        super(context, slim);

        this.model = manager.setModel(key.create(slim)).body();

        addLayers(context);
    }

    protected void addLayers(EntityRendererFactory.Context context) {
        // remove vanilla features (keep modded ones)
        // TODO: test with https://github.com/Globox1997/BackSlot
        features.removeIf(feature -> {
            return feature instanceof ArmorFeatureRenderer
                    || feature instanceof PlayerHeldItemFeatureRenderer
                    || feature instanceof Deadmau5FeatureRenderer
                    || feature instanceof CapeFeatureRenderer
                    || feature instanceof HeadFeatureRenderer
                    || feature instanceof ElytraFeatureRenderer
                    || feature instanceof ShoulderParrotFeatureRenderer;
        });
        addLayer(new ArmourFeature<>(this));
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
        if (manager.getModel().getAttributes().isSitting) {
            stack.translate(0, entity.getHeightOffset(), 0);
        }
    }

    @Override
    public void render(AbstractClientPlayerEntity entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        shadowRadius = manager.getShadowScale();
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
        DebugBoundingBoxRenderer.render(manager.getPony(entity), this, entity, stack, renderContext, tickDelta);

        // Translate the shadow position after everything is done
        // (shadows are drawn after us)
        if (!entity.hasVehicle() && !entity.isSleeping()) {
            float yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
            float l = entity.getWidth() / 2 * manager.getPony(entity).metadata().getSize().getScaleFactor();

            stack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(yaw));
            stack.translate(0, 0, -l);
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
        }
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        manager.preRenderCallback(entity, stack, partialTicks);
        rotationYaw = manager.getRenderYaw(entity, rotationYaw, partialTicks);
        super.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);
        manager.setupTransforms(entity, stack, rotationYaw, partialTicks);
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
        manager.updateModel(player, ModelAttributes.Mode.FIRST_PERSON);

        stack.push();
        float reflect = side == Arm.LEFT ? 1 : -1;

        stack.translate(reflect * 0.1F, -0.54F, 0);

        VertexConsumerProvider interceptedContext = layer -> {
            return renderContext.getBuffer(
                    layer == RenderLayer.getEntitySolid(player.getSkinTexture())
                    ? RenderLayer.getEntityTranslucent(player.getSkinTexture())
                    : layer
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
        return manager.getTexture(player);
    }

    @Override
    public EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getInternalRenderer() {
        return manager;
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayerEntity entity) {
        return IPony.getManager().getPony(entity);
    }

    @Override
    public Identifier getDefaultTexture(AbstractClientPlayerEntity entity, Wearable wearable) {
        return SkinsProxy.instance.getSkin(wearable.getId(), entity).orElseGet(() -> {
            if (wearable.isSaddlebags() && getInternalRenderer().getModel().getMetadata().getRace().supportsLegacySaddlebags()) {
                return manager.getTexture(entity);
            }

            return wearable.getDefaultTexture();
        });
    }
}
