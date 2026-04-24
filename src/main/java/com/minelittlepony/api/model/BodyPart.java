package com.minelittlepony.api.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.mson.util.RenderList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.ArrayList;
import java.util.List;

public enum BodyPart {
    HEAD,
    BODY,
    TAIL,
    NECK,
    LEGS,
    BACK,
    WINGS,
    HORN;

    public <T extends EntityRenderState & PonyModel.AttributedHolder> RenderList createRenderList(TransformedModel<T> transformer) {
        final BodyPart part = this;
        return new RenderList() {
            private final RenderList action = RenderList.of();
            private final List<ModelPart> parts = new ArrayList<>();

            @Nullable
            private T currentState;

            @Override
            public RenderList add(RenderList part) {
                action.add(part);
                return this;
            }

            @Override
            public RenderList add(ModelPart...parts) {
                action.add(parts);
                this.parts.addAll(List.of(parts));
                return this;
            }

            @Override
            public void clear() {
                action.clear();
                parts.clear();
            }

            @Override
            public void accept(PoseStack stack, VertexConsumer vertices, int overlay, int light, int color) {
                stack.pushPose();
                if (currentState != null) {
                    transformer.transform(currentState, part, stack);
                }
                action.accept(stack, vertices, overlay, light, color);
                stack.popPose();
            }

            @SuppressWarnings("unchecked")
            public <S> void pose(S state) {
                currentState = (T)state;
                action.pose(state);
                parts.forEach(o -> currentState.getBodyType().transform(currentState.getAttributes(), part, o));
            }
        };
    }
}
