package com.minelittlepony.model.player;

import net.minecraft.util.math.MathHelper;

import com.minelittlepony.model.components.BugWings;

public class ModelChangeling extends ModelAlicorn {

    public ModelChangeling(boolean smallArms) {
        super(smallArms);
    }

    @Override
    protected void initWings(float yOffset, float stretch) {
        wings = new BugWings<>(this, yOffset, stretch);
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        if (isFlying()) {
            return MathHelper.sin(ticks * 3) + ROTATE_270 + 0.4f;
        }
        return WING_ROT_Z_SNEAK;
    }
}
