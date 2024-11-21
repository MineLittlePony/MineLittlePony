package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.TailShape;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.util.MathUtil;

import java.util.List;
import java.util.stream.IntStream;

public class PonyTail implements SubModel<PonyRenderState>, MsonModel {
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
    public void setPartAngles(PonyRenderState state, float bodySwing) {
        boolean rainboom = state.attributes.isSwimming || state.attributes.isGoingFast;
        tail.roll = rainboom ? 0 : MathHelper.cos(state.limbAmplitudeInverse * 0.8F) * 0.2f * state.limbAmplitudeMultiplier;
        tail.yaw = bodySwing * 5;

        if (state.attributes.isCrouching && !rainboom) {
            tail.setPivot(0, 0, TAIL_SNEAKING_Z);
            tail.pitch = -model.body.pitch + 0.1F;
        } else if (state.attributes.isSitting) {
            tail.pivotZ = TAIL_RIDING_Z;
            tail.pivotY = TAIL_RIDING_Y;
            tail.pitch = MathHelper.PI / 5;
        } else {
            tail.setPivot(0, 0, TAIL_Z);
            if (rainboom) {
                tail.pitch = MathUtil.Angles._90_DEG + MathHelper.sin(state.limbAmplitudeInverse) / 10;
            } else {
                tail.pitch = state.limbAmplitudeMultiplier / 2;

                swingX(state.age);
            }
        }

        if (rainboom) {
            tail.pivotY += 6;
            tail.pivotZ++;
        }

        for (int i = 0; i < segments.size(); i++) {
            segments.get(i).setAngles(i, this, state.attributes);
        }
    }

    private void swingX(float ticks) {
        float sinTickFactor = MathHelper.sin(ticks * 0.067f) * 0.05f;
        tail.pitch += sinTickFactor;
        tail.yaw += sinTickFactor;
    }

    @Override
    public void setVisible(boolean visible, PonyRenderState state) {
        tail.visible = visible;
        tailStop = state.attributes.metadata.tailLength().ordinal();
        shape = state.attributes.metadata.tailShape();
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        stack.push();
        tail.rotate(stack);

        for (int i = 0; i < segments.size(); i++) {
            segments.get(i).render(stack, vertices, i, overlay, light, color);
        }

        stack.pop();
    }

    public static class Segment {
        private final ModelPart tree;
        private TailShape shape;
        private boolean horsey;

        public Segment(ModelPart tree) {
            this.tree = tree;
        }

        public void setAngles(int index, PonyTail tail, ModelAttributes attributes) {
            tree.visible = index >= tail.tailStop;
            shape = tail.shape;
            horsey = attributes.isHorsey;

            if (attributes.isHorsey) {
                tree.pitch = 0.5F;
                HORSEY_TAIL_PIVOT.set(tree);
            } else {
                tree.resetTransform();
            }
        }

        public void render(MatrixStack stack, VertexConsumer renderContext, int index, int overlay, int light, int color) {
            if (!tree.visible) {
                return;
            }

            if (horsey || shape == TailShape.STRAIGHT) {
                tree.yaw = 0;
                tree.render(stack, renderContext, overlay, light, color);
                return;
            }

            stack.push();
            if (shape == TailShape.BUMPY) {
                stack.translate(0, 0, -9/16F);
                float scale = 1 + MathHelper.cos(index + 5) / 2F;
                stack.scale(scale, 1, scale);
                stack.translate(1 / 16F * scale - 0.1F, 0, -2 / 16F * scale);
                tree.pivotZ = 9;
            }
            if (shape == TailShape.SWIRLY) {
                stack.translate(0, 0, -6/16F);
                float scale = 1 + MathHelper.cos(index + 10) / 5F;
                stack.scale(1, 1, scale);
                stack.translate(0, 0, -2 / 16F * scale);
                tree.pivotZ = 9;
            }
            if (shape == TailShape.SPIKY) {
                stack.translate(0, 0, -6/16F);
                float scale = 1 + MathHelper.cos(index + 10) / 5F;
                stack.scale(1, 1, scale);
                stack.translate(0, 0, -2 / 16F * scale);
                tree.yaw = 0.2F * (index % 2 - 1);
                tree.pivotZ = 9;
            }
            tree.render(stack, renderContext, overlay, light, color);
            stack.pop();
        }
    }
}
