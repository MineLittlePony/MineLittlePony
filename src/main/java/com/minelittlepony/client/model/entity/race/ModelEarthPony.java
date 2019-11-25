package com.minelittlepony.client.model.entity.race;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.mson.api.ModelContext;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

public class ModelEarthPony<T extends LivingEntity> extends AbstractPonyModel<T> {

    private final boolean smallArms;

    private ModelPart cape;

    public ModelEarthPony(boolean smallArms) {
        super(smallArms);
        this.smallArms = smallArms;
    }

    @Override
    public void init(ModelContext context) {
        super.init(context);
        cape = context.findByName("cape");
    }

    @Override
    public void setAngles(T entity, float move, float swing, float ticks, float headYaw, float headPitch) {
        super.setAngles(entity, move, swing, ticks, headYaw, headPitch);
        cape.pivotY = isSneaking ? 2 : isRiding ? -4 : 0;
    }

    @Override
    protected float getLegOutset() {
        if (smallArms) {
            return Math.max(1, super.getLegOutset() - 1);
        }
        return super.getLegOutset();
    }
}
