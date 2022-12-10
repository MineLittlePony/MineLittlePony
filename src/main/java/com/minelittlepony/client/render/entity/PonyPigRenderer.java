package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PigEntity;

import com.minelittlepony.api.pony.meta.Wearable;

public class PonyPigRenderer extends PigEntityRenderer {

    public PonyPigRenderer(EntityRendererFactory.Context context) {
        super(context);
        addFeature(new CrownFeature(this));
    }

    private final class CrownFeature extends FeatureRenderer<PigEntity, PigEntityModel<PigEntity>> {
        private final PigEntityModel<PigEntity> model;

        public CrownFeature(FeatureRendererContext<PigEntity, PigEntityModel<PigEntity>> context) {
            super(context);
            model = new PigEntityModel<>(PigEntityModel.getTexturedModelData(new Dilation(0.5F)).createModel());
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PigEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
            if (!entity.hasCustomName() || !entity.getCustomName().getString().equalsIgnoreCase("technoblade")) {
                return;
            }

            getContextModel().copyStateTo(model);
            model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Wearable.CROWN.getDefaultTexture()));
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
    }
}
