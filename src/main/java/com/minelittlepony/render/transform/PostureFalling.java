package com.minelittlepony.render.transform;

import com.minelittlepony.model.AbstractPonyModel;
import net.minecraft.entity.EntityLivingBase;

public class PostureFalling implements PonyPosture<EntityLivingBase> {
    @Override
    public void transform(AbstractPonyModel model, EntityLivingBase entity, double motionX, double motionY, double motionZ, float pitch, float yaw, float ticks) {
        model.motionPitch = 0;
    }
}
