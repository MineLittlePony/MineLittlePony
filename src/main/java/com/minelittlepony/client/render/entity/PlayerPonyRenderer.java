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
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.render.RenderLayerUtil;

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
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;

public class PlayerPonyRenderer
        extends PlayerEntityRenderer
        implements PonyRenderContext<AbstractClientPlayerEntity, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> {
    private final Function<Race, Models<AbstractClientPlayerEntity, ClientPonyModel<PlayerPonyRenderState>>> modelsCache;
    protected final EquineRenderManager<AbstractClientPlayerEntity, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> manager;

    private ModelAttributes.Mode mode = ModelAttributes.Mode.THIRD_PERSON;

    public PlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim);
        modelsCache = Util.memoize(race -> ModelType.getPlayerModel(race).create(slim));
        manager = new EquineRenderManager<>(this, super::setupTransforms, modelsCache.apply(Race.EARTH));
        manager.setModelsLookup(entity -> modelsCache.apply(getPlayerRace(entity)));
        addPonyFeatures(context);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void addPonyFeatures(EntityRendererFactory.Context context) {
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
        addPonyFeature(new ArmourFeature<>(this, context.getModelManager()));
        addPonyFeature(new HeldItemFeature(this, context.getItemRenderer()));
        addPonyFeature(new DJPon3Feature<>(this));
        addPonyFeature(new CapeFeature<>(this));
        addPonyFeature(new SkullFeature<>(this, context.getModelLoader(), context.getItemRenderer()));
        addPonyFeature(new ElytraFeature<>(this));
        addPonyFeature(new PassengerFeature<>(this, context));
        addPonyFeature(new GearFeature<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(FeatureRenderer<? extends PonyRenderState, ? extends ClientPonyModel<? extends PonyRenderState>> feature) {
        return ((List)features).add(feature);
    }

    public Vec3d getPositionOffset(PlayerEntityRenderState state) {
        Vec3d offset = super.getPositionOffset(state);
        return offset.multiply(((PonyRenderState)state).getScaleFactor());
    }

    @Override
    public PlayerEntityRenderState createRenderState() {
        return new PlayerPonyRenderState();
    }

    @Override
    public void updateRenderState(AbstractClientPlayerEntity entity, PlayerEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        manager.preRender(entity, (PlayerPonyRenderState)state, mode);
    }

    public final PlayerPonyRenderState getAndUpdateRenderState(AbstractClientPlayerEntity entity, float tickDelta, ModelAttributes.Mode mode) {
        try {
            this.mode = mode;
            return (PlayerPonyRenderState)getAndUpdateRenderState(entity, tickDelta);
        } finally {
            this.mode = ModelAttributes.Mode.THIRD_PERSON;
        }
    }

    @Override
    public void render(PlayerEntityRenderState state, MatrixStack stack, VertexConsumerProvider vertices, int light) {
        // EntityModelFeatures: We have to force it to use our models otherwise EMF overrides it and breaks pony rendering
        shadowRadius = ((PlayerPonyRenderState)state).getShadowSize();
        super.render(state, stack, vertices, light);
        DebugBoundingBoxRenderer.render((PlayerPonyRenderState)state, stack, vertices);

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

    protected Race getPlayerRace(PlayerPonyRenderState state) {
        return state.getRace();
    }

    @Override
    protected void setupTransforms(PlayerEntityRenderState state, MatrixStack matrices, float animationProgress, float bodyYaw) {
        manager.setupTransforms((PlayerPonyRenderState)state, matrices, animationProgress, bodyYaw);
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
    protected void renderLabelIfPresent(PlayerEntityRenderState state, Text name, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        matrices.push();
        if (state.isInPose(EntityPose.SLEEPING)) {
            if (state.sleepingDirection != null && ((PlayerPonyRenderState)state).sleepingInBed) {
                double bedRad = Math.toRadians(state.sleepingDirection.asRotation());

                matrices.translate(Math.cos(bedRad), 0, -Math.sin(bedRad));
            }
        }
        matrices.translate(0, ((PlayerPonyRenderState)state).nameplateYOffset, 0);
        super.renderLabelIfPresent(state, name, matrices, vertices, light);
        matrices.pop();
    }

    @Override
    public final void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertices, int light, Identifier skinTexture, boolean sleeveVisible) {
        renderArm(matrices, vertices, light, skinTexture, sleeveVisible, Arm.RIGHT);
    }

    @Override
    public final void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertices, int light, Identifier skinTexture, boolean sleeveVisible) {
        renderArm(matrices, vertices, light, skinTexture, sleeveVisible, Arm.LEFT);
    }

    protected void renderArm(MatrixStack stack, VertexConsumerProvider renderContext, int light, Identifier skinTexture, boolean sleeveVisible, Arm side) {
        stack.push();
        float reflect = side == Arm.LEFT ? 1 : -1;

        stack.translate(reflect * 0.1F, -0.54F, 0);

        VertexConsumerProvider interceptedContext = layer -> {
            return renderContext.getBuffer(RenderLayerUtil
                    .getTexture(layer)
                    .filter(skinTexture::equals)
                    .map(i -> RenderLayer.getEntityTranslucent(skinTexture))
                    .orElse(layer)
            );
        };

        if (side == Arm.LEFT) {
            super.renderLeftArm(stack, interceptedContext, light, skinTexture, sleeveVisible);
        } else {
            super.renderRightArm(stack, interceptedContext, light, skinTexture, sleeveVisible);
        }

        stack.pop();
    }

    @Override
    public void setModel(ClientPonyModel<PlayerPonyRenderState> model) {
        this.model = model;
    }

    @Override
    public EquineRenderManager<AbstractClientPlayerEntity, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> getInternalRenderer() {
        return manager;
    }

    @Override
    public Pony getEntityPony(AbstractClientPlayerEntity entity) {
        return Pony.getManager().getPony(entity);
    }

    @Override
    public Identifier getTexture(PlayerEntityRenderState state) {
        return ((PonyRenderState)state).pony.texture();
    }

    @Override
    public Identifier getDefaultTexture(PlayerPonyRenderState state, Wearable wearable) {
        if (state.wearabledTextures.containsKey(wearable)) {
            return state.wearabledTextures.get(wearable);
        }

        if (wearable.isSaddlebags() && state.getRace().supportsLegacySaddlebags()) {
            return getTexture(state);
        }

        return wearable.getDefaultTexture();
    }
}
