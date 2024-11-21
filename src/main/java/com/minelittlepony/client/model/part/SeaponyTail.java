package com.minelittlepony.client.model.part;

import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.MsonModel;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

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
    public void setPartAngles(PonyRenderState state, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {
        float rotation = state.attributes.isLyingDown ? 0 : MathHelper.sin(animationProgress * 0.536f) / 4;

        tailBase.pitch = MathHelper.HALF_PI + rotation;
        tailTip.pitch = rotation;
        tailFins.pitch = rotation - MathHelper.HALF_PI;

        float turn = MathHelper.clamp(state.attributes.motionRoll * 0.05F + bodySwing, -0.4F, 0.4F);

        tailBase.yaw = turn;
        turn /= 2F;
        tailTip.roll = -turn;
        turn /= 2F;
        tailFins.roll = -turn;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        tailBase.render(stack, vertices, overlay, light, color);
    }

}
