package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class ChangelingModel<T extends PonyRenderState> extends AlicornModel<T> {

    public ChangelingModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public boolean wingsAreOpen(T state) {
        return (state.attributes.isFlying || state.attributes.isCrouching) && !state.attributes.isGliding;
    }

    @Override
    public float getWingRotationFactor(T state, float ticks) {
        return state.attributes.isFlying ? MathHelper.sin(ticks * 3) + WINGS_HALF_SPREAD_ANGLE : WINGS_RAISED_ANGLE;
    }
}
