package com.minelittlepony.api.model;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.util.MathUtil;

public interface WingedPonyModel<T extends LivingEntity> extends PonyModel<T> {
    public static final float WINGS_HALF_SPREAD_ANGLE = MathUtil.Angles._270_DEG;
    public static final float WINGS_FULL_SPREAD_ANGLE = MathUtil.Angles._270_DEG + 0.4F;
    public static final float WINGS_RAISED_ANGLE = 4;

    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen() {
        return (getAttributes().isSwimming || getAttributes().isFlying || getAttributes().isCrouching)
            && (PonyConfig.getInstance().flappyElytras.get() || !getAttributes().isGliding);
    }

    default boolean isBurdened() {
        return isWearing(Wearable.SADDLE_BAGS_BOTH)
                || isWearing(Wearable.SADDLE_BAGS_LEFT)
                || isWearing(Wearable.SADDLE_BAGS_RIGHT);
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
    default float getWingRotationFactor(float ticks) {
        return getAttributes().wingAngle;
    }

}
