package com.minelittlepony.api.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.HumanoidArm;

import com.mojang.blaze3d.vertex.PoseStack;

public interface TransformedModel<T extends EntityRenderState & PonyModel.AttributedHolder> {
    ModelPart getBodyPart(BodyPart part);

    /**
     * Applies a transform particular to a certain body part.
     */
    default void transform(T state, BodyPart part, PoseStack matrices) {}

    default void transformHeldItem(T state, HumanoidArm arm, PoseStack matrices) {}

    /**
     * Applies transformations to align to a certain body part.
     */
    default void transformAccessory(T state, BodyPart part, PoseStack matrices) {
        transform(state, part, matrices);
        getBodyPart(part).translateAndRotate(matrices);
    }

    interface BodyType {
        void transform(ModelAttributes attributes, BodyPart part, PoseStack stack);

        void transform(ModelAttributes attributes, BodyPart bodyPart, ModelPart part);
    }
}
