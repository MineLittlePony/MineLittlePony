package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.render.RenderLayerUtil;

import java.util.*;

import com.minelittlepony.client.render.EquineRenderManager;

import net.minecraft.client.item.ItemModelManager;
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
import net.minecraft.util.math.*;

public class PlayerPonyRenderer
        extends PlayerEntityRenderer
        implements PonyRenderContext<
            AbstractClientPlayerEntity,
            PlayerPonyRenderState,
            ClientPonyModel<PlayerPonyRenderState>
        > {
    protected final EquineRenderManager<AbstractClientPlayerEntity, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> manager;

    private ModelAttributes.Mode mode = ModelAttributes.Mode.THIRD_PERSON;
    protected final ItemModelManager itemModelManager;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PlayerPonyRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim);
        itemModelManager = context.getItemModelManager();
        manager = new EquineRenderManager<>(this, super::setupTransforms, race -> ModelType.getPlayerModel(race).create(slim));

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
        addPonyFeature(new ArmourFeature<>(this, context.getEquipmentModelLoader()));
        addPonyFeature(new HeldItemFeature<>(this));
        addPonyFeature(new DJPon3Feature<>(this));
        addFeature(new CapeFeature(this, context.getEntityModels(), context.getEquipmentModelLoader()));
        addPonyFeature(new SkullFeature<>(this, context.getEntityModels(), HeadFeatureRenderer.HeadTransformation.DEFAULT, true));
        addPonyFeature(new ElytraFeature(this, context.getEquipmentRenderer()));
        addPonyFeature(new PassengerFeature<>(this, context));
        addPonyFeature(new GearFeature<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final boolean addPonyFeature(FeatureRenderer<
            ? extends PlayerEntityRenderState,
            ? extends ClientPonyModel<? extends PonyRenderState>
            > feature) {
        return ((List)features).add(feature);
    }

    @Override
    public Vec3d getPositionOffset(PlayerEntityRenderState state) {
        Vec3d offset = super.getPositionOffset(state);
        return offset
                .multiply(((PlayerPonyRenderState)state).attributes.size.scaleFactor())
                .add(0, state.baseScale * ((PlayerPonyRenderState)state).yOffset, 0);
    }

    @Override
    public PlayerEntityRenderState createRenderState() {
        return new PlayerPonyRenderState();
    }

    @Override
    public void updateRenderState(AbstractClientPlayerEntity entity, PlayerEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        manager.updateState(entity, (PlayerPonyRenderState)state, mode, itemModelManager);
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
    protected void setupTransforms(PlayerEntityRenderState state, MatrixStack matrices, float animationProgress, float bodyYaw) {
        manager.completeStateUpdate(state);
        shadowRadius = ((PlayerPonyRenderState)state).attributes.size.shadowSize();
        manager.setupTransforms((PlayerPonyRenderState)state, matrices, animationProgress, bodyYaw);
    }

    @Override
    protected Box getBoundingBox(AbstractClientPlayerEntity entity) {
        return manager.getBoundingBox(entity, entity.getBoundingBox());
    }

    @Override
    protected void renderLabelIfPresent(PlayerEntityRenderState state, Text name, MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        matrices.push();
        if (state.isInPose(EntityPose.SLEEPING)) {
            if (state.sleepingDirection != null && ((PlayerPonyRenderState)state).sleepingInBed) {
                double bedRad = Math.toRadians(state.sleepingDirection.getPositiveHorizontalDegrees());

                matrices.translate(Math.cos(bedRad), 0, -Math.sin(bedRad));
            }
        }
        matrices.translate(0, ((PlayerPonyRenderState)state).nameplateYOffset, 0);
        super.renderLabelIfPresent(state, name, matrices, vertices, light);
        matrices.pop();
        DebugBoundingBoxRenderer.render((PlayerPonyRenderState)state, matrices, vertices);
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
    public EquineRenderManager<AbstractClientPlayerEntity, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> getEquineManager() {
        return manager;
    }

    @Override
    public Pony getEntityPony(AbstractClientPlayerEntity entity) {
        return Pony.getManager().getPony(entity);
    }

    @Override
    public final Identifier getTexture(PlayerEntityRenderState state) {
        return ((PlayerPonyRenderState)state).pony.texture();
    }

    @Override
    public Identifier getDefaultTexture(PlayerPonyRenderState state, Wearable wearable) {
        if (state.wearabledTextures.containsKey(wearable)) {
            return state.wearabledTextures.get(wearable);
        }

        if (wearable.isSaddlebags() && state.race.supportsLegacySaddlebags()) {
            return getTexture(state);
        }

        return wearable.getDefaultTexture();
    }
}
