package com.minelittlepony.api.model;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;

import com.minelittlepony.util.MathUtil;

public interface ModelWithWings<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {
    public static final float WINGS_HALF_SPREAD_ANGLE = MathUtil.Angles._270_DEG;
    public static final float WINGS_FULL_SPREAD_ANGLE = MathUtil.Angles._270_DEG + 0.4F;
    public static final float WINGS_RAISED_ANGLE = 4;

    /**
     * Gets the wings of this pegasus/flying creature
     */
    SubModel<T> getWings();

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
