package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;

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
        public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PigEntityRenderState state, float limbDistance, float limbAngle) {
            if (state.displayName != null && state.displayName.getString().equalsIgnoreCase("technoblade")) {
                renderModel(model, Wearable.CROWN.getDefaultTexture(), matrices, queue, light, state, Colors.WHITE, 0);
            }
        }
    }
}
