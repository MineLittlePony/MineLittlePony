package com.minelittlepony.transform;

import com.minelittlepony.model.AbstractPonyModel;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class PostureElytra implements PonyPosture<EntityLivingBase> {
    @Override
    public void transform(AbstractPonyModel model, EntityLivingBase entity, double motionX, double motionY, double motionZ, float pitch, float yaw, float ticks) {
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, entity.isSneaking() ? 0.2F : -1, 0);
    }
}
