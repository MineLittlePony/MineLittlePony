package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class ChangelingModel<T extends PonyRenderState> extends AlicornModel<T> {

    public ChangelingModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public boolean wingsAreOpen(ModelAttributes state) {
        return (state.isFlying || state.isCrouching) && !state.isGliding;
    }

    @Override
    public float getWingRotationFactor(ModelAttributes state, float ticks) {
        if (state.isFlying) {
            return MathHelper.sin(ticks * 3) + WINGS_HALF_SPREAD_ANGLE;
        }
        return WINGS_RAISED_ANGLE;
    }
}
