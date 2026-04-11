package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.object.armorstand.ArmorStandModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Rotations;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyDisplayTags;
import com.minelittlepony.api.model.ModelAttributes.Mode;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.SizePreset;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PonyArmorStandEntityArmorModel;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PonifiedRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.Untyped;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.Optional;

public class PonyStandRenderer extends LivingEntityRenderer<ArmorStand, PonyStandRenderer.State, ArmorStandModel> {
    static final Pony PONY = new Pony(Identifier.withDefaultNamespace("null"), () -> Optional.of(PonyData.NULL));

    private final PonifiedContext context = new PonifiedContext();
    private final ItemModelResolver itemModelManager;

    public static boolean isPonyStand(Entity entity) {
        return PonyDisplayTags.of(entity).shouldPonify(entity.hasCustomName() && "Ponita".equals(entity.getCustomName().getString()));
    }

    public PonyStandRenderer(EntityRendererProvider.Context context) {
        super(context, ModelType.ARMOUR_STAND.createModel(), 0);
        itemModelManager = context.getItemModelResolver();
        addLayer(new PonifiedFeature<>(this, new ArmourFeature<>(this.context, context.getEquipmentAssets(), context.getAtlas(Sheets.ARMOR_TRIMS_SHEET))));
        addLayer(new PonifiedFeature<>(this, new HeldItemFeature<>(this.context)));
        addLayer(new PonifiedFeature<>(this, new ElytraFeature<>(this.context, context.getEquipmentRenderer())));
        addLayer(new PonifiedFeature<>(this, new SkullFeature<>(this.context, context.getPlayerSkinRenderCache(), context.getModelSet(), CustomHeadLayer.Transforms.DEFAULT, false)));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public Identifier getTextureLocation(State state) {
        return ArmorStandRenderer.DEFAULT_SKIN_LOCATION;
    }

    @Override
    public void extractRenderState(ArmorStand entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state.ponyState, tickDelta, itemModelManager);
        state.yRot = entity.getYRot(tickDelta);
        state.isMarker = entity.isMarker();
        state.isSmall = entity.isSmall();
        state.showArms = true;
        state.showBasePlate = entity.showBasePlate();
        state.bodyPose = entity.getBodyPose();
        state.headPose = entity.getHeadPose();
        state.leftArmPose = entity.getLeftArmPose();
        state.rightArmPose = entity.getRightArmPose();
        state.leftLegPose = entity.getLeftLegPose();
        state.rightLegPose = entity.getRightLegPose();
        state.wiggle = (float)(entity.level().getGameTime() - entity.lastHit) + tickDelta;

        if (state.leftLegPose.equals(ArmorStand.DEFAULT_LEFT_LEG_POSE)) {
            state.leftLegPose = new Rotations(-state.leftArmPose.x(), state.leftArmPose.y(), state.leftArmPose.z());
        }

        if (state.rightLegPose.equals(ArmorStand.DEFAULT_RIGHT_LEG_POSE)) {
            state.rightLegPose = new Rotations(-state.rightArmPose.x(), state.rightArmPose.y(), state.rightArmPose.z());
        }

        context.manager.updateState(entity, state.ponyState, Mode.OTHER, itemModelManager);
        state.ponyState.isBaby = state.isSmall;
        state.ponyState.attributes.size = state.isSmall ? SizePreset.FOAL : SizePreset.NORMAL;
        state.xRot = entity.getHeadPose().x() * Mth.DEG_TO_RAD;
        state.yRot = entity.getHeadPose().y() * Mth.DEG_TO_RAD;
    }

    @Override
    protected void setupRotations(State state, PoseStack matrices, float bodyRot, float entityScale) {

        context.manager.setupTransforms(state.ponyState, matrices, bodyRot, entityScale);

        matrices.mulPose(Axis.YP.rotationDegrees(180 - bodyRot));
        if (state.wiggle < 5) {
            matrices.mulPose(Axis.YP.rotationDegrees(Mth.sin(state.wiggle / 1.5F * (float) Math.PI) * 3.0F));
        }
        matrices.translate(0, 0, state.scale * -4/16F);
    }

    @Override
    protected boolean shouldShowName(ArmorStand entity, double squaredDistanceToCamera) {
        return entity.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderType getRenderType(State state, boolean showBody, boolean translucent, boolean appearGlowing) {
        if (!state.appearsGlowing()) {
            return super.getRenderType(state, showBody, translucent, appearGlowing);
        }

        Identifier texture = getTextureLocation(state);
        if (translucent) {
            return RenderTypes.entityTranslucent(texture, false);
        }

        return showBody ? RenderTypes.entityCutout(texture, false) : null;
    }

    private class PonifiedContext implements
                RenderLayerParent<PonyState, EarthPonyModel<PonyState>>,
                PonyRenderContext<ArmorStand, PonyState, EarthPonyModel<PonyState>> {
        private final EquineRenderManager<ArmorStand, PonyState, EarthPonyModel<PonyState>> manager
            = new EquineRenderManager<>(this, (_, _, _, _) -> {},
                    ModelType.EARTH_PONY.<EarthPonyModel<PonyState>>create(false).withArmorFactory(PonyArmorStandEntityArmorModel::new));

        @Override
        public Pony getEntityPony(ArmorStand entity) {
            return PONY;
        }

        @Override
        public EquineRenderManager<ArmorStand, PonyState, EarthPonyModel<PonyState>> getEquineManager() {
            return manager;
        }

        @Override
        public EarthPonyModel<PonyState> getModel() {
            return getEquineManager().lookupModel(Race.EARTH).body();
        }
    }

    private class PonifiedFeature<S extends PonyRenderState, T extends RenderLayer<?, ?>> extends RenderLayer<PonyStandRenderer.State, ArmorStandModel> {
        private final T feature;

        public PonifiedFeature(RenderLayerParent<PonyStandRenderer.State, ArmorStandModel> context, T feature) {
            super(context);
            this.feature = feature;
        }

        @Override
        public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, PonyStandRenderer.State state, float headYaw, float headPitch) {
            Untyped.<T, RenderLayer<PonyState, ?>>cast(feature).submit(matrices, frame, light, state.ponyState, headYaw, headPitch);
        }
    }

    public static final class State extends ArmorStandRenderState implements PonifiedRenderState {
        public PonyState ponyState = new PonyState(this);
    }

    public static class PonyState extends PonyRenderState {
        public final ArmorStandRenderState angles;
        public PonyState(ArmorStandRenderState state) {
            this.angles = state;
        }
    }
}
