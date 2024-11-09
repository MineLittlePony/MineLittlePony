package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.pony.meta.Wearable;

public class PonyPigRenderer extends PigEntityRenderer {
    public PonyPigRenderer(EntityRendererFactory.Context context) {
        super(context);
        addFeature(new CrownFeature(this));
    }

    private final class CrownFeature extends FeatureRenderer<PigEntityRenderState, PigEntityModel> {
        private final PigEntityModel model;

        public CrownFeature(FeatureRendererContext<PigEntityRenderState, PigEntityModel> context) {
            super(context);
            model = new PigEntityModel(PigEntityModel.getTexturedModelData(new Dilation(0.5F)).createModel());
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PigEntityRenderState entity, float limbDistance, float limbAngle) {
            if (entity.customName == null || !entity.customName.getString().equalsIgnoreCase("technoblade")) {
                return;
            }

            model.setAngles(entity);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Wearable.CROWN.getDefaultTexture()));
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
