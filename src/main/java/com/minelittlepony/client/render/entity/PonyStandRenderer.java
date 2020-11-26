package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.client.render.entity.feature.ArmourFeature;
import com.minelittlepony.model.armour.ArmourLayer;

public class PonyStandRenderer extends ArmorStandEntityRenderer {

    public PonyStandRenderer(EntityRendererFactory.Context context) {
        super(context);

        features.clear();
        addFeature(new Armour(this, context));
        addFeature(new HeldItemFeatureRenderer<>(this));
        addFeature(new ElytraFeatureRenderer<>(this, context.getModelLoader()));
        addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader()));
    }

    class Armour extends ArmorFeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel> {
        private final ModelWrapper<ArmorStandEntity, EarthPonyModel<ArmorStandEntity>> pony = new ModelWrapper<>(ModelType.EARTH_PONY.getKey(false));

        public Armour(FeatureRendererContext<ArmorStandEntity, ArmorStandArmorEntityModel> renderer, EntityRendererFactory.Context context) {
            super(renderer,
                    new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_INNER_ARMOR)),
                    new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR))
            );

            pony.apply(new PonyData(Race.EARTH));
        }

        @Override
        public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, ArmorStandEntity entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
            if (entity.hasCustomName() && "Ponita".equals(entity.getCustomName().asString())) {

                headPitch = 0.017453292F * entity.getHeadRotation().getPitch();
                headYaw = 0.017453292F * entity.getHeadRotation().getYaw();

                pony.getBody().animateModel(entity, limbDistance, limbAngle, tickDelta);
                pony.getBody().setAngles(entity, limbDistance, limbAngle, age, headYaw, headPitch);

                for (EquipmentSlot i : EquipmentSlot.values()) {
                    if (i.getType() == EquipmentSlot.Type.ARMOR) {
                        ArmourFeature.renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER);
                        ArmourFeature.renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER);
                    }
                }
            } else {
                super.render(stack, renderContext, lightUv, entity, limbDistance, limbAngle, tickDelta, age, headYaw, headPitch);
            }
        }
    }
}
