package com.minelittlepony.api.model;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.mson.util.RenderList;

import java.util.function.Supplier;

public interface SubModel<T extends EntityRenderState> extends RenderList {
    static <T extends EntityRenderState> RenderList toRenderList(Supplier<SubModel<? super T>> part) {
        return (stack, vertices, overlay, light, color) -> part.get().renderPart(stack, vertices, overlay, light, color);
    }

    /**
     * Renders this model component.
     */
    void renderPart(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color);

    @Override
    default void accept(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
        renderPart(stack, vertices, overlay, light, color);
    }

    /**
     * Sets the model's various rotation angles.
     */
    default void setPartAngles(T state, float wobbleAmount) {
    }

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible, T state) {

    }
}
