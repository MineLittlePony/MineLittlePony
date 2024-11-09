package com.minelittlepony.api.model;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.util.MathUtil;

public interface WingedPonyModel<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends PonyModel<T> {
    public static final float WINGS_HALF_SPREAD_ANGLE = MathUtil.Angles._270_DEG;
    public static final float WINGS_FULL_SPREAD_ANGLE = MathUtil.Angles._270_DEG + 0.4F;
    public static final float WINGS_RAISED_ANGLE = 4;

    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen(T state) {
        return (state.getAttributes().isSwimming || state.getAttributes().isFlying || state.getAttributes().isCrouching)
            && (PonyConfig.getInstance().flappyElytras.get() || !state.getAttributes().isGliding);
    }

    default boolean isBurdened(T state) {
        return state.getAttributes().isWearing(Wearable.SADDLE_BAGS_BOTH)
                || state.getAttributes().isWearing(Wearable.SADDLE_BAGS_LEFT)
                || state.getAttributes().isWearing(Wearable.SADDLE_BAGS_RIGHT);
    }

    /**
     * Gets the wings of this pegasus/flying creature
     */
    SubModel getWings();

    /**
     * Determines angle used to animate wing flaps whilst flying/swimming.
     *
     * @param ticks Partial render ticks
     */
    default float getWingRotationFactor(T state, float ticks) {
        return state.getAttributes().wingAngle;
    }

}
