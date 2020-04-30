package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.gear.SaddleBags;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager.Mode;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.DJPon3Feature;
import com.minelittlepony.client.render.entity.feature.PassengerFeature;
import com.minelittlepony.client.render.entity.feature.GearFeature;
import com.minelittlepony.client.render.entity.feature.GlowingItemFeature;
import com.minelittlepony.client.render.entity.feature.ArmourFeature;
import com.minelittlepony.client.render.entity.feature.CapeFeature;
import com.minelittlepony.client.render.entity.feature.SkullFeature;
import com.minelittlepony.client.render.entity.feature.ElytraFeature;
import com.minelittlepony.mson.api.ModelKey;

import java.util.List;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class PlayerPonyRenderer extends PlayerEntityRenderer implements IPonyRenderContext<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> {

    protected final EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager = new EquineRenderManager<>(this);

    public PlayerPonyRenderer(EntityRenderDispatcher dispatcher, boolean slim, ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> key) {
        super(dispatcher, slim);

        this.model = manager.setModel(key).getBody();

        addLayers();
    }

    protected void addLayers() {
        features.clear();

        addLayer(new DJPon3Feature<>(this));
        addLayer(new ArmourFeature<>(this));
        addFeature(new StuckArrowsFeatureRenderer<>(this));
        addLayer(new SkullFeature<>(this));
        addLayer(new ElytraFeature<>(this));
        addLayer(new GlowingItemFeature<>(this));
        addLayer(new CapeFeature<>(this));
        addLayer(new PassengerFeature<>(this));
        addLayer(new GearFeature<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected boolean addLayer(FeatureRenderer<AbstractClientPlayerEntity, ? extends ClientPonyModel<AbstractClientPlayerEntity>> feature) {
        return ((List)features).add(feature);
    }

    @Override
    protected void scale(AbstractClientPlayerEntity entity, MatrixStack stack, float tickDelta) {
        if (getModel() instanceof PlayerEntityModel) {
            ((PlayerEntityModel<?>)getModel()).setVisible(true);
        }

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
            float l = entity.getWidth() / 2 * manager.getPony(entity).getMetadata().getSize().getScaleFactor();

            stack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(yaw));
            stack.translate(0, 0, -l);
        }
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        manager.preRenderCallback(entity, stack, partialTicks);
        rotationYaw = manager.getRenderYaw(entity, rotationYaw, partialTicks);
        super.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);

        manager.applyPostureTransform(entity, stack, rotationYaw, partialTicks);
    }

    @Override
    public boolean shouldRender(AbstractClientPlayerEntity entity, Frustum camera, double camX, double camY, double camZ) {
        if (entity.isSleeping() && entity == MinecraftClient.getInstance().player) {
            return MinecraftClient.getInstance().options.perspective != 0
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
        manager.updateModel(player, Mode.FIRST_PERSON);

        stack.push();
        float reflect = side == Arm.LEFT ? 1 : -1;

        stack.translate(reflect * 0.1F, -0.54F, 0);

        if (side == Arm.LEFT) {
            super.renderLeftArm(stack, renderContext, lightUv, player);
        } else {
            super.renderRightArm(stack, renderContext, lightUv, player);
        }

        stack.pop();
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        return manager.getPony(player).getTexture();
    }

    @Override
    public ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getModelWrapper() {
        return manager.playerModel;
    }

    @Override
    public EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getInternalRenderer() {
        return manager;
    }

    @Override
    public Identifier findTexture(AbstractClientPlayerEntity entity) {
        return getTexture(entity);
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayerEntity entity) {
        return MineLittlePony.getInstance().getManager().getPony(entity);
    }

    @Override
    public Identifier getDefaultTexture(AbstractClientPlayerEntity entity, Wearable wearable) {
        if (wearable == Wearable.SADDLE_BAGS) {
            if (getInternalRenderer().getModel().getMetadata().getRace() == Race.BATPONY) {
                return SaddleBags.TEXTURE;
            }
        }

        return IPonyRenderContext.super.getDefaultTexture(entity, wearable);
    }
}
