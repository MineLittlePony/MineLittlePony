package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.ModelPart;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class PonyArmourModel<T extends PonyRenderState> extends AbstractPonyModel<T> {
    public PonyArmourModel(ModelPart tree) {
        super(tree, false);
    }

    @Override
    protected void alignArmForAction(T state, ModelPart arm, ArmPose pose, ArmPose complement, float sigma) {
        if (!state.hasMagicGlow()) {
            super.alignArmForAction(state, arm, pose, complement, sigma);
        }
    }

    @Override
    protected void swingArm(T state, ModelPart arm) {
        if (!state.hasMagicGlow()) {
            super.swingArm(state, arm);
        }
    }

    @Override
    protected void animateBreathing(T state) {
        if (!state.hasMagicGlow()) {
            super.animateBreathing(state);
        }
    }
}
