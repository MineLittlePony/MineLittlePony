package com.minelittlepony.api.model;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.mson.util.RenderList;

public interface SubModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends RenderList {
    /**
     * Renders this model component.
     */
    default void render(PonyModel<T> model, T state, MatrixStack matrices, OrderedRenderCommandQueue queue) {}

    @Override
    default void accept(MatrixStack matrices, VertexConsumer vertices, int overlay, int light, int color) {}

    /**
     * Sets the model's various rotation angles.
     */
    default void setAngles(PonyModel<T> model, T state) {
    }

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible, T state) {

    }
}
