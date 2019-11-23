package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.AbstractPonyModel;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public class ModelEarthPony<T extends LivingEntity> extends AbstractPonyModel<T> {

    private final boolean smallArms;

    public ModelPart bipedCape;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        bipedCape.pivotY = isSneaking ? 2 : isRiding ? -4 : 0;
    }

    @Override
    protected float getLegOutset() {
        if (smallArms) {
            if (attributes.isSleeping) return 2.6f;
            if (attributes.isCrouching) return 1;
            return 4;
        }
        return super.getLegOutset();
    }
}
