package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.ModelAttributes.Mode;
import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.PonifiedEquipmentRenderer;
import com.minelittlepony.client.model.entity.PonyArmourStandModel;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PonyStandRenderer extends ArmorStandEntityRenderer implements PonyRenderContext<ArmorStandEntity, PonyRenderState, EarthPonyModel<PonyRenderState>> {
    static final Pony PONY = new Pony(Identifier.ofVanilla("null"), () -> Optional.of(PonyData.NULL));
    private final PonyArmourStandModel pony = ModelType.ARMOUR_STAND.createModel();
    private final Models<EarthPonyModel<PonyRenderState>> models = ModelType.EARTH_PONY.create(false);

    private final ArmorStandArmorEntityModel human;

    private final EquineRenderManager<ArmorStandEntity, PonyRenderState, EarthPonyModel<PonyRenderState>> manager;

    public PonyStandRenderer(EntityRendererFactory.Context context) {
        super(context);
        human = model;
        this.manager = new EquineRenderManager<>(this, (state, stack, progress, yaw) -> {}, models);

        Predicate<ArmorStandEntityRenderState> swapPredicate = state -> ((State)state).hasPonyForm;
        Function<ArmorStandEntityRenderState, PonyRenderState> converter = state -> ((State)state).ponyState;

        for (int i = 0; i < features.size(); i++) {
            var feature = features.get(i);
            if (feature instanceof ArmorFeatureRenderer) {
                features.set(i, new SwappableFeature<>(this, feature, new Armour(context), swapPredicate, converter));
            }
            if (feature instanceof ElytraFeatureRenderer) {
                features.set(i, new SwappableFeature<>(this,
                        feature,
                        new ElytraFeature<>(() -> models.body(), context.getEquipmentRenderer()), swapPredicate, converter));
            }
            if (feature instanceof HeadFeatureRenderer) {
                features.set(i, new SwappableFeature<>(this,
                        feature,
                        new SkullFeature<PonyRenderState, EarthPonyModel<PonyRenderState>>(
                                this,
                                context.getModelLoader(),
                                context.getItemRenderer(),
                                HeadFeatureRenderer.HeadTransformation.DEFAULT,
                                false
                        ), swapPredicate, converter));
            }
        }
    }


    @Override
    public Identifier getDefaultTexture(PonyRenderState entity, Wearable wearable) {
        return wearable.getDefaultTexture();
    }

    @Override
    public Pony getEntityPony(ArmorStandEntity entity) {
        return PONY;
    }

    @Override
    public EquineRenderManager<ArmorStandEntity, PonyRenderState, EarthPonyModel<PonyRenderState>> getInternalRenderer() {
        return manager;
    }

    @Override
    public void setModel(EarthPonyModel<PonyRenderState> model) { }

    @Override
    public ArmorStandEntityRenderState createRenderState() {
        return new State();
    }

    public void updateRenderState(ArmorStandEntity entity, ArmorStandEntityRenderState state, float tickDelta) {
        boolean ponified = entity.hasCustomName() && "Ponita".equals(entity.getCustomName().getString());

        super.updateRenderState(entity, state, tickDelta);
        ((State)state).hasPonyForm = ponified;
        if (ponified) {
            BipedEntityRenderer.updateBipedRenderState(entity, ((State)state).ponyState, tickDelta);
            manager.updateState(entity, ((State)state).ponyState, Mode.OTHER);
            state.pitch = 0.017453292F * entity.getHeadRotation().getPitch();
            state.yawDegrees = 0.017453292F * entity.getHeadRotation().getYaw();
        }
    }

    @Override
    protected void setupTransforms(ArmorStandEntityRenderState state, MatrixStack stack, float animationProgress, float bodyYaw) {
        super.setupTransforms(state, stack, animationProgress, bodyYaw);
        if (((State)state).hasPonyForm) {
            stack.translate(0, 0, state.baseScale * -4/16F);
            this.model = pony;
        } else {
            this.model = human;
        }
    }

    class Armour extends FeatureRenderer<PonyRenderState, EarthPonyModel<PonyRenderState>> {
        private final PonifiedEquipmentRenderer equipmentRenderer;

        public Armour(EntityRendererFactory.Context context) {
            super(() -> models.body());
            equipmentRenderer = new PonifiedEquipmentRenderer(context.getEquipmentModelLoader());
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, PonyRenderState state, float limbAngle, float limbDistance) {
            getContextModel().setAngles(state);
            ArmourFeature.renderArmor(models, matrices, vertices, light, state, limbDistance, limbAngle, equipmentRenderer);
        }
    }

    public static final class State extends ArmorStandEntityRenderState {
        public boolean hasPonyForm;
        public PonyRenderState ponyState = new PonyRenderState();
    }
}
