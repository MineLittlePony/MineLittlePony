package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.mson.api.MsonModel;

public interface PonyModel<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends MsonModel, ModelWithHooves, ModelWithHat, ModelWithHead {

    ModelPart getBodyPart(BodyPart part);

    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(T state, BodyPart part, MatrixStack stack);

    public interface AttributedHolder {
        ModelAttributes getAttributes();
    }
}
