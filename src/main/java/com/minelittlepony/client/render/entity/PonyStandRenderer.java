package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.ModelAttributes.Mode;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PonyArmourStandModel;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.Optional;

public class PonyStandRenderer extends LivingEntityRenderer<ArmorStandEntity, PonyStandRenderer.State, PonyArmourStandModel> {
    static final Pony PONY = new Pony(Identifier.ofVanilla("null"), () -> Optional.of(PonyData.NULL));

    private final PonifiedContext context = new PonifiedContext();

    public static boolean isPonyStand(Entity entity) {
        return entity.hasCustomName() && "Ponita".equals(entity.getCustomName().getString());
    }

    public PonyStandRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ARMOUR_STAND.createModel(), 0);
        addFeature(new PonifiedFeature(this, new ArmourFeature<>(this.context, context.getEquipmentModelLoader())));
        addFeature(new PonifiedFeature(this, new HeldItemFeature<>(this.context, context.getItemRenderer())));
        addFeature(new PonifiedFeature(this, new ElytraFeature<>(this.context, context.getEquipmentRenderer())));
        addFeature(new PonifiedFeature(this, new SkullFeature<>(this.context, context.getModelLoader(), context.getItemRenderer(), HeadFeatureRenderer.HeadTransformation.DEFAULT, false)));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public Identifier getTexture(State state) {
        return ArmorStandEntityRenderer.TEXTURE;
    }

    public void updateRenderState(ArmorStandEntity entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        BipedEntityRenderer.updateBipedRenderState(entity, state.ponyState, tickDelta);
        state.yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw, entity.getYaw());
        state.marker = entity.isMarker();
        state.small = entity.isSmall();
        state.showArms = true;
        state.showBasePlate = entity.shouldShowBasePlate();
        state.bodyRotation = entity.getBodyRotation();
        state.headRotation = entity.getHeadRotation();
        state.leftArmRotation = entity.getLeftArmRotation();
        state.rightArmRotation = entity.getRightArmRotation();
        state.leftLegRotation = entity.getLeftLegRotation();
        state.rightLegRotation = entity.getRightLegRotation();
        state.timeSinceLastHit = (float)(entity.getWorld().getTime() - entity.lastHitTime) + tickDelta;

        if (state.leftLegRotation.equals(ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION)) {
            state.leftLegRotation = new EulerAngle(-state.leftArmRotation.getPitch(), state.leftArmRotation.getYaw(), state.leftArmRotation.getRoll());
        }

        if (state.rightLegRotation.equals(ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION)) {
            state.rightLegRotation = new EulerAngle(-state.rightArmRotation.getPitch(), state.rightArmRotation.getYaw(), state.rightArmRotation.getRoll());
        }

        context.manager.updateState(entity, state.ponyState, Mode.OTHER);
        state.ponyState.baby = state.small;
        state.ponyState.attributes.size = state.small ? SizePreset.FOAL : SizePreset.NORMAL;
        state.ponyState.equippedHeadStack = state.equippedHeadStack;
        state.pitch = MathHelper.RADIANS_PER_DEGREE * entity.getHeadRotation().getPitch();
        state.yawDegrees = MathHelper.RADIANS_PER_DEGREE * entity.getHeadRotation().getYaw();
    }

    @Override
    protected void setupTransforms(State state, MatrixStack matrices, float animationProgress, float bodyYaw) {

        context.manager.setupTransforms(state.ponyState, matrices, animationProgress, bodyYaw);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - animationProgress));
        if (state.timeSinceLastHit < 5) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(state.timeSinceLastHit / 1.5F * (float) Math.PI) * 3.0F));
        }
        matrices.translate(0, 0, state.baseScale * -4/16F);
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity entity, double squaredDistanceToCamera) {
        return entity.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderLayer getRenderLayer(State state, boolean showBody, boolean translucent, boolean showOutline) {
        if (!state.marker) {
            return super.getRenderLayer(state, showBody, translucent, showOutline);
        }

        Identifier identifier = getTexture(state);
        if (translucent) {
            return RenderLayer.getEntityTranslucent(identifier, false);
        }

        return showBody ? RenderLayer.getEntityCutoutNoCull(identifier, false) : null;
    }

    private class PonifiedContext implements
                FeatureRendererContext<PonyRenderState, EarthPonyModel<PonyRenderState>>,
                PonyRenderContext<ArmorStandEntity, PonyRenderState, EarthPonyModel<PonyRenderState>> {
        private final EquineRenderManager<ArmorStandEntity, PonyRenderState, EarthPonyModel<PonyRenderState>> manager
            = new EquineRenderManager<>(this, (state, stack, progress, yaw) -> {}, ModelType.EARTH_PONY.create(false));

        @Override
        public Pony getEntityPony(ArmorStandEntity entity) {
            return PONY;
        }

        @Override
        public EquineRenderManager<ArmorStandEntity, PonyRenderState, EarthPonyModel<PonyRenderState>> getEquineManager() {
            return manager;
        }

        @Override
        public EarthPonyModel<PonyRenderState> getModel() {
            return manager.getModels().body();
        }
    }

    private class PonifiedFeature extends FeatureRenderer<PonyStandRenderer.State, PonyArmourStandModel> {
        private final FeatureRenderer<?, ?> feature;

        public PonifiedFeature(FeatureRendererContext<PonyStandRenderer.State, PonyArmourStandModel> context,
                FeatureRenderer<?, ?> feature) {
            super(context);
            this.feature = feature;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, PonyStandRenderer.State state, float limbAngle, float limbDistance) {
            if (context.getModel() instanceof BipedEntityModel bipedModel) {
                model.copyTransforms(bipedModel);
            }
            ((FeatureRenderer)feature).render(matrices, vertices, light, state.ponyState, limbAngle, limbDistance);
        }
    }

    public static final class State extends ArmorStandEntityRenderState {
        public PonyRenderState ponyState = new PonyRenderState();
    }
}
