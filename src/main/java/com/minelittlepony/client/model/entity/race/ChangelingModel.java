package com.minelittlepony.client.model.entity.race;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class ChangelingModel<T extends LivingEntity> extends AlicornModel<T> {

    public ChangelingModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    @Override
    public boolean wingsAreOpen() {
        return (getAttributes().isFlying || getAttributes().isCrouching) && !getAttributes().isGliding;
    }

    @Override
    public float getWingRotationFactor(float ticks) {
        if (getAttributes().isFlying) {
            return MathHelper.sin(ticks * 3) + WINGS_HALF_SPREAD_ANGLE;
        }
        return WINGS_RAISED_ANGLE;
    }
}
