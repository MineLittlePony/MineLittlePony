package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.common.util.animation.Interpolator;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;

import java.util.UUID;

public class LionTail implements IPart, MsonModel {

    private ModelPart tail;
    private IPonyModel<?> model;

    public LionTail(ModelPart tree) {
        tail = tree.getChild("tail");
    }

    @Override
    public void init(ModelContext context) {
        model = context.getModel();
    }

    @Override
    public void setRotationAndAngles(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {

        float baseSail = 1F;

        float speed = swing > 0.01F ? 6 : 90;
        Interpolator interpolator = Interpolator.linear(interpolatorId);

        float straightness = 1.6F * (1 + (float)Math.sin(ticks / speed) / 8F);
        float bend = (float)Math.sin(Math.PI/2F + 2 * ticks / speed) / 16F;

        if (model.getAttributes().isCrouching) {
            baseSail += 1;
            straightness += 0.5F;
        }

        straightness = interpolator.interpolate("kirin_tail_straightness", straightness, 10);
        bend = interpolator.interpolate("kirin_tail_bendiness", bend, 10);

        tail.pitch = baseSail;
        tail.pitch += swing / 2;
        tail.yaw = bend;
        tail.roll = bodySwing * 2;

        float sinTickFactor = MathHelper.sin(ticks * 0.067f) * 0.05f;
        tail.pitch += sinTickFactor;
        tail.yaw += sinTickFactor;

        var tail2 = tail.getChild("tail2");
        tail2.pitch = -(baseSail + sinTickFactor) / straightness;
        tail2.yaw = bend;
        tail2.roll = bodySwing;

        var tail3 = tail2.getChild("tail3");
        tail3.pitch = tail2.pitch / straightness;
        tail3.yaw = tail2.yaw;
        tail3.roll = -bodySwing;

        var tail4 = tail3.getChild("tail4");
        tail4.pitch = tail3.pitch / straightness;
        tail4.yaw = -tail3.yaw * 7F;
        tail4.roll = -bodySwing;

        var tail5 = tail4.getChild("tail5");
        tail5.pitch = -tail4.pitch * straightness;
        tail5.yaw = -tail4.yaw * 2F;
        tail5.roll = -bodySwing * 2;

        var tail6 = tail5.getChild("tail6");
        tail6.pitch = tail5.pitch * straightness;
        tail6.yaw = tail5.yaw;
        tail6.roll = -bodySwing * 2F;
    }

    @Override
    public void setVisible(boolean visible) {
        tail.visible = visible;
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId) {
        tail.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha);
    }
}
