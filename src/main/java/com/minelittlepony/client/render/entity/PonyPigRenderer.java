package com.minelittlepony.client.render.entity;

import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.util.CommonColors;

import com.minelittlepony.api.pony.meta.Wearable;
import com.mojang.blaze3d.vertex.PoseStack;

public class PonyPigRenderer extends PigRenderer {
    public PonyPigRenderer(EntityRendererProvider.Context context) {
        super(context);
        addLayer(new CrownFeature(this));
    }

    private final class CrownFeature extends RenderLayer<PigRenderState, PigModel> {
        private final PigModel model;

        public CrownFeature(RenderLayerParent<PigRenderState, PigModel> context) {
            super(context);
            model = new PigModel(PigModel.createBodyLayer(new CubeDeformation(0.5F)).bakeRoot());
        }

        @Override
        public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, PigRenderState state, float yRot, float xRot) {
            if (state.nameTag != null && state.nameTag.getString().equalsIgnoreCase("technoblade")) {
                coloredCutoutModelCopyLayerRender(model, Wearable.CROWN.getDefaultTexture(), matrices, frame, light, state, CommonColors.WHITE, 0);
            }
        }
    }
}
