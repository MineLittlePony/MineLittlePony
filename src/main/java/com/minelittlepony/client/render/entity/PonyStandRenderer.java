package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.PonifiedEquipmentRenderer;
import com.minelittlepony.client.model.entity.PonyArmourStandModel;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.render.entity.feature.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyStandRenderer extends ArmorStandEntityRenderer {
    private final PonyArmourStandModel pony = ModelType.ARMOUR_STAND.createModel();
    private final ArmorStandArmorEntityModel human;

    public PonyStandRenderer(EntityRendererFactory.Context context) {
        super(context);
        human = model;

        for (int i = 0; i < features.size(); i++) {
            var feature = features.get(i);
            if (feature instanceof ArmorFeatureRenderer) {
                features.set(i, new SwappableFeature<>(this, feature, new Armour(this, context), state -> ((State)state).hasPonyForm));
            }
            if (feature instanceof ElytraFeatureRenderer) {
                features.set(i, new SwappableFeature<>(this, feature, new ElytraFeature<>(this, context.getEquipmentRenderer()), state -> ((State)state).hasPonyForm));
            }
        }

        //addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getItemRenderer()));
    }

    @Override
    public ArmorStandEntityRenderState createRenderState() {
        return new State();
    }

    public void updateRenderState(ArmorStandEntity entity, ArmorStandEntityRenderState state, float tickDelta) {
        boolean ponified = entity.hasCustomName() && "Ponita".equals(entity.getCustomName().getString());

        super.updateRenderState(entity, state, tickDelta);
        ((State)state).hasPonyForm = ponified;
        if (ponified) {
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

    class Armour extends FeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {
        private final Models<EarthPonyModel<PonyRenderState>> pony = ModelType.EARTH_PONY.create(false);

        private final PonifiedEquipmentRenderer equipmentRenderer;

        public Armour(FeatureRendererContext<ArmorStandEntityRenderState, ArmorStandArmorEntityModel> renderer, EntityRendererFactory.Context context) {
            super(renderer);
            equipmentRenderer = new PonifiedEquipmentRenderer(context.getEquipmentModelLoader());
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, ArmorStandEntityRenderState state, float limbAngle, float limbDistance) {
            pony.body().setAngles(((State)state).ponyState);
            ArmourFeature.renderArmor(pony, matrices, vertices, light, ((State)state).ponyState, limbDistance, limbAngle, equipmentRenderer);
        }
    }

    public static final class State extends ArmorStandEntityRenderState {
        public boolean hasPonyForm;
        public PonyRenderState ponyState = new PonyRenderState();
    }
}
