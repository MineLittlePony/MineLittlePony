package com.minelittlepony.api.model;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.HumanoidArm;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.mson.api.MsonModel;
import com.mojang.blaze3d.vertex.PoseStack;

public interface PonyModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends MsonModel, ModelWithHooves<T>, HeadedModel {

    ModelPart getBodyPart(BodyPart part);

    /**
     * Applies a transform particular to a certain body part.
     */
    default void transform(T state, BodyPart part, PoseStack matrices) { }

    default void transform(T state, BodyPart bodyPart, ModelPart part) { }

    default void transformHeldItem(T state, HumanoidArm arm, PoseStack matrices) {}

    /**
     * Applies transformations to align to a certain body part.
     */
    default void transformAccessory(T state, BodyPart part, PoseStack matrices) {
        transform(state, part, matrices);
        getBodyPart(part).translateAndRotate(matrices);
    }

    public interface AttributedHolder {
        ModelAttributes getAttributes();

        Race getRace();

        float getSwingAmount();

        /**
         * Tests if this model is wearing the given piece of gear.
         */
        boolean isWearing(Wearable wearable);
    }
}
