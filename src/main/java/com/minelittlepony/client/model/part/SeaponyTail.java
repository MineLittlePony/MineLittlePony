package com.minelittlepony.client.model.part;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.MsonModel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class SeaponyTail implements SubModel<PonyRenderState>, MsonModel {
    private final ModelPart tailBase;

    private final ModelPart tailTip;
    private final ModelPart tailFins;

    public SeaponyTail(ModelPart tree) {
        tailBase = tree.getChild("base");
        tailTip = tailBase.getChild("tip");
        tailFins = tailTip.getChild("fins");
    }

    @Override
    public void setAngles(PonyModel<PonyRenderState> model, PonyRenderState state) {
        float rotation = state.attributes.isLyingDown ? 0 : Mth.sin(state.ageInTicks * 0.536F) / 4;

        tailBase.xRot = Mth.HALF_PI + rotation;
        tailTip.xRot = rotation;
        tailFins.xRot = rotation - Mth.HALF_PI;

        float turn = Mth.clamp(state.attributes.motionRoll * 0.05F + state.wobbleAmount, -0.4F, 0.4F);

        tailBase.yRot = turn;
        turn /= 2F;
        tailTip.zRot = -turn;
        turn /= 2F;
        tailFins.zRot = -turn;
    }

    @Override
    public void accept(PoseStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        tailBase.render(stack, vertices, overlay, light, color);
    }

}
