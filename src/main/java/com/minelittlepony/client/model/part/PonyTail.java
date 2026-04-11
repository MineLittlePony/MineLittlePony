package com.minelittlepony.client.model.part;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.TailShape;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.util.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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
    public void setAngles(PonyModel<PonyRenderState> model, PonyRenderState state) {
        boolean rainboom = state.attributes.isSwimming || state.attributes.isGoingFast;
        tail.zRot = rainboom ? 0 : Mth.cos(state.speedValue * 0.8F) * 0.2f * state.walkAnimationPos;
        tail.yRot = state.wobbleAmount * 5;

        if (state.attributes.isCrouching && !rainboom) {
            tail.setPos(0, 0, TAIL_SNEAKING_Z);
            tail.xRot = -model.getBodyPart(BodyPart.BODY).xRot + 0.1F;
        } else if (state.attributes.isSitting) {
            tail.z = TAIL_RIDING_Z;
            tail.y = TAIL_RIDING_Y;
            tail.xRot = Mth.PI / 5;
        } else {
            tail.setPos(0, 0, TAIL_Z);
            if (rainboom) {
                tail.xRot = MathUtil.Angles._90_DEG + Mth.sin(state.speedValue) / 10;
            } else {
                tail.xRot = state.walkAnimationPos / 2;

                swingX(state.ageInTicks);
            }
        }

        if (rainboom) {
            tail.y += 6;
            tail.z++;
        }

        for (int i = 0; i < segments.size(); i++) {
            segments.get(i).setAngles(i, this, state.attributes);
        }
    }

    private void swingX(float ticks) {
        float sinTickFactor = Mth.sin(ticks * 0.067f) * 0.05f;
        tail.xRot += sinTickFactor;
        tail.yRot += sinTickFactor;
    }

    @Override
    public void setVisible(boolean visible, PonyRenderState state) {
        tail.visible = visible;
        tailStop = state.attributes.metadata.tailLength().ordinal();
        shape = state.attributes.metadata.tailShape();
    }

    @Override
    public void accept(PoseStack matrices, VertexConsumer vertices, int overlay, int light, int color) {
        if (tail.visible) {
            matrices.pushPose();
            model.body.translateAndRotate(matrices);
            tail.translateAndRotate(matrices);

            for (int i = 0; i < segments.size(); i++) {
                segments.get(i).render(matrices, vertices, i, overlay, light, color);
            }

            matrices.popPose();
        }
    }

    public static class Segment {
        private final ModelPart tree;
        private TailShape shape;
        private boolean horsey;

        public Segment(ModelPart tree) {
            this.tree = tree;
        }

        public void setAngles(int index, PonyTail tail, ModelAttributes attributes) {
            tree.visible = index < tail.tailStop;
            shape = tail.shape;
            horsey = attributes.isHorsey;

            if (attributes.isHorsey) {
                tree.xRot = 0.5F;
                HORSEY_TAIL_PIVOT.set(tree);
            } else {
                tree.resetPose();
            }
        }

        public void render(PoseStack matrices, VertexConsumer vertices, int index, int overlay, int light, int color) {
            if (!tree.visible) {
                return;
            }

            if (horsey || shape == TailShape.STRAIGHT) {
                tree.yRot = 0;
                tree.render(matrices, vertices, overlay, light, color);
                return;
            }

            matrices.pushPose();
            if (shape == TailShape.BUMPY) {
                matrices.translate(0, 0, -9/16F);
                float scale = 1 + Mth.cos(index + 5) / 2F;
                matrices.scale(scale, 1, scale);
                matrices.translate(1 / 16F * scale - 0.1F, 0, -2 / 16F * scale);
                tree.z = 9;
            }
            if (shape == TailShape.SWIRLY) {
                matrices.translate(0, 0, -6/16F);
                float scale = 1 + Mth.cos(index + 10) / 5F;
                matrices.scale(1, 1, scale);
                matrices.translate(0, 0, -2 / 16F * scale);
                tree.z = 9;
            }
            if (shape == TailShape.SPIKY) {
                matrices.translate(0, 0, -6/16F);
                float scale = 1 + Mth.cos(index + 10) / 5F;
                matrices.scale(1, 1, scale);
                matrices.translate(0, 0, -2 / 16F * scale);
                tree.yRot = 0.2F * (index % 2 - 1);
                tree.z = 9;
            }
            tree.render(matrices, vertices, overlay, light, color);
            matrices.popPose();
        }
    }
}
