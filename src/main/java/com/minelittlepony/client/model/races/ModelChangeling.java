package com.minelittlepony.client.model.races;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.components.BugWings;

public class ModelChangeling<T extends LivingEntity> extends ModelAlicorn<T> {

    public ModelChangeling(boolean smallArms) {
        super(smallArms);
    }

    @Override
    protected void initWings(float yOffset, float stretch) {
        wings = new BugWings<>(this, yOffset, stretch);
    }

    @Override
    public boolean wingsAreOpen() {
        return (isFlying() || attributes.isCrouching) && !getAttributes().isGliding;
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        if (isFlying()) {
            return MathHelper.sin(ticks * 3) + ROTATE_270;
        }
        return WING_ROT_Z_SNEAK;
    }
}
