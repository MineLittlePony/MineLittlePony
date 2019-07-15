package com.minelittlepony.client.transform;

import net.minecraft.entity.LivingEntity;

import com.minelittlepony.model.IModel;
import com.mojang.blaze3d.platform.GlStateManager;

public class PostureElytra implements PonyPosture<LivingEntity> {
    @Override
    public void transform(IModel model, LivingEntity entity, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        GlStateManager.rotatef(90, 1, 0, 0);
        GlStateManager.translatef(0, model.getAttributes().isCrouching ? 0.2F : -1, 0);
    }
}
