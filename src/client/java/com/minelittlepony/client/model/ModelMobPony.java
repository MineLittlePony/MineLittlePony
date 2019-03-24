package com.minelittlepony.client.model;

import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.races.ModelAlicorn;

/**
 * Common class for all humanoid (ponioid?) non-player enemies.
 *
 */
public abstract class ModelMobPony extends ModelAlicorn {

    public ModelMobPony() {
        super(false);
    }

    /**
     * Rotates the provided arm to the correct orientation for holding an item.
     *
     * @param arm           The arm to rotate
     * @param direction     Direction multiplier. 1 for right, -1 for left.
     * @param swingProgress How far we are through the current swing
     * @param ticks         Render partial ticks
     */
    protected void rotateArmHolding(ModelRenderer arm, float direction, float swingProgress, float ticks) {
        float swing = MathHelper.sin(swingProgress * PI);
        float roll = MathHelper.sin((1 - (1 - swingProgress) * (1 - swingProgress)) * PI);

        float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
        float sin = MathHelper.sin(ticks * 0.067F) / 10;

        arm.rotateAngleX = -1.5707964F;
        arm.rotateAngleX -= swing * 1.2F - roll * 0.4F;
        arm.rotateAngleX += sin;

        arm.rotateAngleY = direction * (0.1F - swing * 0.6F);
        arm.rotateAngleZ = cos;
    }
}
