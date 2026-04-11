package com.minelittlepony.api.model;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import com.minelittlepony.util.MathUtil;

public interface ModelWithWings<T extends HumanoidRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {
    public static final float WINGS_HALF_SPREAD_ANGLE = MathUtil.Angles._270_DEG;
    public static final float WINGS_FULL_SPREAD_ANGLE = MathUtil.Angles._270_DEG + 0.4F;
    public static final float WINGS_RAISED_ANGLE = 4;

    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen(T state) {
        return state.getAttributes().wingsSpread;
    }

    /**
     * Determines angle used to animate wing flaps whilst flying/swimming.
     *
     * @param ticks Partial render ticks
     */
    default float getWingRotationFactor(T state) {
        return state.getAttributes().wingAngle;
    }
}
