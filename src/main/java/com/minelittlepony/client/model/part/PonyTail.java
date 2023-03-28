package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.meta.TailShape;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class PonyTail implements IPart, MsonModel {
    private static final float TAIL_Z = 14;
    private static final float TAIL_RIDING_Y = 3;
    private static final float TAIL_RIDING_Z = 13;
    private static final float TAIL_SNEAKING_Z = 15;

    private ModelPart tail;
    private AbstractPonyModel<?> model;

    private int tailStop = 0;
    private TailShape shape = TailShape.STRAIGHT;

    private final List<Segment> segments = new ArrayList<>();

    public PonyTail(ModelPart tree) {
        tail = tree.getChild("tail");
    }

    @Override
    public void init(ModelView context) {
        model = context.getModel();

        int segments = (int)context.getLocalValue("segments", 4);

        for (int i = 0; i < segments; i++) {
            Segment segment = context.findByName("segment_" + i);
            segment.tail = this;
            segment.index = i;
            this.segments.add(segment);
        }
    }

    @Override
    public void setRotationAndAngles(ModelAttributes attributes, float move, float swing, float bodySwing, float ticks) {
        boolean rainboom = attributes.isSwimming || attributes.isGoingFast;
        tail.roll = rainboom ? 0 : MathHelper.cos(move * 0.8F) * 0.2f * swing;
        tail.yaw = bodySwing * 5;

        if (model.getAttributes().isCrouching && !rainboom) {
            tail.setPivot(0, 0, TAIL_SNEAKING_Z);
            tail.pitch = -model.body.pitch + 0.1F;
        } else if (model.getAttributes().isSitting) {
            tail.pivotZ = TAIL_RIDING_Z;
            tail.pivotY = TAIL_RIDING_Y;
            tail.pitch = MathHelper.PI / 5;
        } else {
            tail.setPivot(0, 0, TAIL_Z);
            if (rainboom) {
                tail.pitch = MathUtil.Angles._90_DEG + MathHelper.sin(move) / 10;
            } else {
                tail.pitch = swing / 2;

                swingX(ticks);
            }
        }

        if (rainboom) {
            tail.pivotY += 6;
            tail.pivotZ++;
        }
    }

    private void swingX(float ticks) {
        float sinTickFactor = MathHelper.sin(ticks * 0.067f) * 0.05f;
        tail.pitch += sinTickFactor;
        tail.yaw += sinTickFactor;
    }

    @Override
    public void setVisible(boolean visible, ModelAttributes attributes) {
        tail.visible = visible;
        tailStop = model.getMetadata().getTailLength().ordinal();
        shape = model.getMetadata().getTailShape();
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
        stack.push();
        tail.rotate(stack);

        segments.forEach(segment -> segment.render(stack, vertices, overlayUv, lightUv, red, green, blue, alpha));

        stack.pop();
    }

    public static class Segment {
        public PonyTail tail;

        public int index;

        private final ModelPart tree;

        public Segment(ModelPart tree) {
            this.tree = tree;
        }

        public void render(MatrixStack stack, VertexConsumer renderContext, int overlayUv, int lightUv, float red, float green, float blue, float alpha) {
            if (index >= tail.tailStop) {
                return;
            }

            if (tail.shape == TailShape.STRAIGHT) {
                tree.yaw = 0;
                tree.pivotZ = 0;
                tree.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
                return;
            }

            stack.push();
            if (tail.shape == TailShape.BUMPY) {
                stack.translate(0, 0, -9/16F);
                float scale = 1 + MathHelper.cos(index + 5) / 2F;
                stack.scale(scale, 1, scale);
                stack.translate(1 / 16F * scale - 0.1F, 0, -2 / 16F * scale);
                tree.pivotZ = 9;
            }
            if (tail.shape == TailShape.SWIRLY) {
                stack.translate(0, 0, -6/16F);
                float scale = 1 + MathHelper.cos(index + 10) / 5F;
                stack.scale(1, 1, scale);
                stack.translate(0, 0, -2 / 16F * scale);
                tree.pivotZ = 9;
            }
            if (tail.shape == TailShape.SPIKY) {
                stack.translate(0, 0, -6/16F);
                float scale = 1 + MathHelper.cos(index + 10) / 5F;
                stack.scale(1, 1, scale);
                stack.translate(0, 0, -2 / 16F * scale);
                tree.yaw = 0.2F * (index % 2 - 1);
                tree.pivotZ = 9;
            }
            tree.render(stack, renderContext, overlayUv, lightUv, red, green, blue, alpha);
            stack.pop();
        }
    }
}
