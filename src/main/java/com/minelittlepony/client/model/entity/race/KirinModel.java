package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.geom.ModelPart;

import com.minelittlepony.api.model.Pivot;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class KirinModel<T extends PonyRenderState> extends UnicornModel<T> {

    private final ModelPart beard;

    public KirinModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
        beard = neck.getChild("beard");
    }

    @Override
    protected void adjustBody(T state, float pitch, Pivot pivot) {
        super.adjustBody(state, pitch, pivot);
        beard.resetPose();
        beard.xRot -= neck.xRot;
    }
}
