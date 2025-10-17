package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.mson.api.MsonModel;

public interface PonyModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends MsonModel, ModelWithHooves<T>, ModelWithHead {

    ModelPart getBodyPart(BodyPart part);

    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(T state, BodyPart part, MatrixStack matrices);

    /**
     * Applies transformations to align to a certain body part.
     */
    default void transformAccessory(T state, BodyPart part, MatrixStack matrices) {
        transform(state, part, matrices);
        getBodyPart(part).applyTransform(matrices);
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
