package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.TailShape;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.util.MathUtil;

import java.util.List;
import java.util.stream.IntStream;

public class PonyTail implements IPart, MsonModel {
    private static final float TAIL_Z = 14;
    private static final float TAIL_RIDING_Y = 3;
    private static final float TAIL_RIDING_Z = 13;
    private static final float TAIL_SNEAKING_Z = 15;

    private static final Pivot HORSEY_TAIL_PIVOT = new Pivot(0, 6, -6);

    private ModelPart tail;
    private AbstractPonyModel<?> model;

    private int tailStop = 0;
    private TailShape shape = TailShape.STRAIGHT;

    private List<Segment> segments = List.of();

    public PonyTail(ModelPart tree) {
        tail = tree.getChild("tail");
    }

    @Override
    public void init(ModelView context) {
        model = context.getModel();
        segments = IntStream.range(0, (int)context.getLocalValue("segments", 4))
                .mapToObj(i -> context.<Segment>findByName("segment_" + i))
                .toList();
    }

    @Override
    public void setPartAngles(ModelAttributes attributes, float limbAngle, float limbSpeed, float bodySwing, float animationProgress) {
        boolean rainboom = attributes.isSwimming || attributes.isGoingFast;
        tail.roll = rainboom ? 0 : MathHelper.cos(limbAngle * 0.8F) * 0.2f * limbSpeed;
        tail.yaw = bodySwing * 5;

        if (attributes.isCrouching && !rainboom) {
            tail.setPivot(0, 0, TAIL_SNEAKING_Z);
            tail.pitch = -model.body.pitch + 0.1F;
        } else if (attributes.isSitting) {
            tail.pivotZ = TAIL_RIDING_Z;
            tail.pivotY = TAIL_RIDING_Y;
            tail.pitch = MathHelper.PI / 5;
        } else {
            tail.setPivot(0, 0, TAIL_Z);
            if (rainboom) {
                tail.pitch = MathUtil.Angles._90_DEG + MathHelper.sin(limbAngle) / 10;
            } else {
                tail.pitch = limbSpeed / 2;

                swingX(animationProgress);
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
        tailStop = attributes.metadata.getTailLength().ordinal();
        shape = attributes.metadata.getTailShape();
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
        stack.push();
        tail.rotate(stack);

        for (int i = 0; i < segments.size(); i++) {
            segments.get(i).render(this, stack, vertices, i, overlayUv, lightUv, red, green, blue, alpha, attributes);
        }

        stack.pop();
    }

    public static class Segment {
        private final ModelPart tree;

        public Segment(ModelPart tree) {
            this.tree = tree;
        }

        public void render(PonyTail tail, MatrixStack stack, VertexConsumer renderContext, int index, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
            if (index >= tail.tailStop) {
                return;
            }

            if (attributes.isHorsey) {
                tree.pitch = 0.5F;
                HORSEY_TAIL_PIVOT.set(tree);
            } else {
                tree.resetTransform();
            }

            if (attributes.isHorsey || tail.shape == TailShape.STRAIGHT) {
                tree.yaw = 0;
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
