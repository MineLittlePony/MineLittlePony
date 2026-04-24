package com.minelittlepony.api.model;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.minelittlepony.mson.util.RenderList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public interface SubModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends RenderList {
    /**
     * Renders this model component.
     */
    default void render(PonyModel<T> model, T state, PoseStack matrices, SubmitNodeCollector frame) {}

    @Override
    default void accept(PoseStack matrices, VertexConsumer vertices, int overlay, int light, int color) {}

    /**
     * Sets the model's various rotation angles.
     */
    default void setAngles(PonyModel<T> model, T state) {
    }

    default void setHidden() {}
}
