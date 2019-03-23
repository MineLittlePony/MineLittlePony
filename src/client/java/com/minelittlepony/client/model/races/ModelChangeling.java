package com.minelittlepony.client.model.races;

import net.minecraft.util.math.MathHelper;

import com.minelittlepony.client.model.components.BugWings;
import com.minelittlepony.pony.meta.Wearable;

public class ModelChangeling extends ModelAlicorn {

    public ModelChangeling(boolean smallArms) {
        super(smallArms);
    }

    @Override
    protected void initWings(float yOffset, float stretch) {
        wings = new BugWings<>(this, yOffset, stretch);
    }

    @Override
    public boolean wingsAreOpen() {
        return (isFlying() || isCrouching()) && !isElytraFlying();
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        if (isFlying()) {
            return MathHelper.sin(ticks * 3) + ROTATE_270;
        }
        return WING_ROT_Z_SNEAK;
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        if (wearable == Wearable.SADDLE_BAGS) {
            return false;
        }

        return super.isWearing(wearable);
    }
}
