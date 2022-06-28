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
