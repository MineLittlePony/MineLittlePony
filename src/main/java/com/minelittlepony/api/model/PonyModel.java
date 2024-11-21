package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.mson.api.MsonModel;

public interface PonyModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends MsonModel, ModelWithHooves, ModelWithHat, ModelWithHead {

    ModelPart getBodyPart(BodyPart part);

    /**
     * Applies a transform particular to a certain body part.
     */
    void transform(T state, BodyPart part, MatrixStack stack);

    public interface AttributedHolder {
        ModelAttributes getAttributes();

        Race getRace();

        float getSwingAmount();
    }
}
