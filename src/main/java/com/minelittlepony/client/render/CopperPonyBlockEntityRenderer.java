package com.minelittlepony.client.render;

import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.Oxidizable.OxidationLevel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.block.entity.CopperGolemStatueBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.CopperGolemStatueBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SpikeModel;

public class CopperPonyBlockEntityRenderer extends CopperGolemStatueBlockEntityRenderer {

    private final SpikeModel.BlockModel model = new SpikeModel.BlockModel(ModelType.SPIKE.createTree().get());

    public CopperPonyBlockEntityRenderer(Context context) {
        super(context);
    }

    public void render(CopperGolemStatueBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera) {
        if (state.blockState.getBlock() instanceof CopperGolemStatueBlock block) {
            matrices.push();
            matrices.translate(0.5F, 0.0F, 0.5F);

            var oxidation = block.getOxidationLevel();
            Identifier texture = MineLittlePony.id("textures/entity/copper_golem/" + (oxidation == OxidationLevel.UNAFFECTED ? "" : oxidation.asString() + "_") + "copper_golem_dragon.png");

            RenderLayer renderLayer = RenderLayer.getEntityCutoutNoCull(texture);
            queue.submitModel(model, state, matrices, renderLayer, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0, state.crumblingOverlay);
            matrices.pop();
        }
    }
}
