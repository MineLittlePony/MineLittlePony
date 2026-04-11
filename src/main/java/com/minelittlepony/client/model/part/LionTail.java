package com.minelittlepony.client.model.part;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.SubModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.animation.Interpolator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class LionTail implements SubModel<PonyRenderState> {

    private ModelPart tail;

    public LionTail(ModelPart tree) {
        tail = tree.getChild("tail");
    }

    @Override
    public void setAngles(PonyModel<PonyRenderState> model, PonyRenderState state) {
        tail.resetPose();

        float bodySwing = state.wobbleAmount * 5;

        float baseSail = 1F;

        float speed = state.walkAnimationPos > 0.01F ? 6 : 90;
        Interpolator interpolator = state.attributes.getMainInterpolator();

        float straightness = 1.6F * (1 + (float)Math.sin(state.ageInTicks / speed) / 8F);
        float twist = (float)Math.sin(Math.PI/2F + 2 * state.ageInTicks / speed) / 16F;
        float bend = state.attributes.motionRoll / 80F;

        if (state.attributes.isCrouching) {
            baseSail += 1;
            straightness += 0.5F;
        }

        if (state.attributes.isGoingFast || state.attributes.isSwimming) {
            straightness *= 2;
        }

        straightness = interpolator.interpolate("kirin_tail_straightness", straightness, 10);
        twist = interpolator.interpolate("kirin_tail_twist", twist, 10);
        bend = interpolator.interpolate("kirin_tail_bendiness", bend, 10);

        tail.xRot = baseSail;
        tail.xRot += state.walkAnimationPos / 2;
        tail.yRot = twist;
        tail.zRot = bodySwing * 2;

        float sinTickFactor = Mth.sin(state.ageInTicks * 0.067f) * 0.05f;
        tail.xRot += sinTickFactor;
        tail.yRot += sinTickFactor;

        var tail2 = tail.getChild("tail2");
        tail2.setRotation(-(baseSail + sinTickFactor) / straightness, twist, bodySwing);

        var tail3 = tail2.getChild("tail3");
        tail3.setRotation(tail2.xRot / straightness, tail2.yRot, -bodySwing);

        var tail4 = tail3.getChild("tail4");
        tail4.setRotation(tail3.xRot / straightness, -tail3.yRot * 7, -bodySwing);

        var tail5 = tail4.getChild("tail5");
        tail5.setRotation(-tail4.xRot * straightness, -tail4.yRot * 2, -bodySwing * 2);

        var tail6 = tail5.getChild("tail6");
        tail6.setRotation(tail5.xRot * straightness, tail5.yRot, -bodySwing * 2);

        tail3.zRot += bend;
        tail4.zRot += bend;
        tail5.zRot += bend;
        tail6.zRot += bend;

        if (state.attributes.isHorsey) {
            tail.z = 14;
            tail.y = 7;
        }
    }

    @Override
    public void setVisible(boolean visible, PonyRenderState state) {
        tail.visible = visible;
    }

    @Override
    public void accept(PoseStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        tail.render(stack, vertices, overlay, light, color);
    }
}
