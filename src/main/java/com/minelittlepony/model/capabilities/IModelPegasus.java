package com.minelittlepony.model.capabilities;

import static com.minelittlepony.model.PonyModelConstants.LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
import static com.minelittlepony.model.PonyModelConstants.ROTATE_270;

import net.minecraft.util.math.MathHelper;

public interface IModelPegasus extends IModel {
    /**
     * Returns true if the wings are spread.
     */
    default boolean wingsAreOpen() {
        return isSwimming() || isFlying() || isCrouching();
    }


    default float getWingRotationFactor(float ticks) {
        if (isSwimming()) {
            return (MathHelper.sin(ticks * 0.136f) / 2) + ROTATE_270;
        }
        if (isFlying()) {
            return MathHelper.sin(ticks * 0.536f) + ROTATE_270 + 0.4f;
        }
        return LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
    }

}
