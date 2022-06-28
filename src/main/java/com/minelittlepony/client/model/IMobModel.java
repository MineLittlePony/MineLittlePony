package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.model.PonyModelConstants;

/**
 * Common interface for all undead enemies.
 */
public interface IMobModel {

    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     *
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param ticks         Render partial ticks
     */
    default void rotateArmHolding(ModelPart arm, float direction, float swingProgress, float ticks) {
        float swing = MathHelper.sin(swingProgress * PonyModelConstants.PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * PonyModelConstants.PI);

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) / 10;

        arm.pitch = -1.5707964F;
        arm.pitch -= swing * 1.2F - roll * 0.4F;
        arm.pitch += sin;

        arm.yaw = direction * (0.1F - swing * 0.6F);
        arm.roll = cos;
    }
}
