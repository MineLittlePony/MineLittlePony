package com.minelittlepony.common.model;

import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.common.model.PonyModelConstants.ROTATE_270;
import static com.minelittlepony.common.model.PonyModelConstants.WING_ROT_Z_SNEAK;

public interface IPegasus extends IModel {

    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen() {
        return (isSwimming() || isFlying() || isCrouching()) && !isElytraFlying();
    }

    /**
     * Determines angle used to animate wing flaps whilst flying/swimming.
     *
     * @param ticks Partial render ticks
     */
    default float getWingRotationFactor(float ticks) {
        if (isSwimming()) {
            return (MathHelper.sin(ticks * 0.136f) / 2) + ROTATE_270;
        }
        if (isFlying()) {
            return MathHelper.sin(ticks * 0.536f) + ROTATE_270 + 0.4f;
        }
        return WING_ROT_Z_SNEAK;
    }

}
