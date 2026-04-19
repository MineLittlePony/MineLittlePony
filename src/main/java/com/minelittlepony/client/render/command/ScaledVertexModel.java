package com.minelittlepony.client.render.command;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.rendertype.RenderType;

import com.minelittlepony.client.render.command.MagicOverlayRenderCommandQueue.Pass;
import com.minelittlepony.mson.util.PartUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;

class ScaledVertexModel<S> extends Model<S> {
    private final Model<? super S> model;
    private final List<Pass> passes;
    private final RenderType layer;

    public ScaledVertexModel(Model<? super S> model, List<Pass> passes, RenderType layer) {
        super(PartUtil.EMPTY_PART, _ -> layer);
        this.model = model;
        this.passes = passes;
        this.layer = layer;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        var scaledVertices = new ScaledVertexConsumer(vertices, layer, color, matrices);
        for (var pass : passes) {
            matrices.pushPose();
            matrices.translate(pass.translation().scale(MagicOverlayRenderCommandQueue.TRANSLATION_SCALE));
            model.renderToBuffer(matrices, scaledVertices.setScale(pass.scale()), light, overlay, color);
            matrices.popPose();
        }
    }

    @Override
    public void setupAnim(S state) {
        model.setupAnim(state);
    }
}