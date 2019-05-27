package com.minelittlepony.client.transform;

import com.minelittlepony.model.IModel;
import com.minelittlepony.util.transform.MotionCompositor;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;

public class PostureFlight extends MotionCompositor implements PonyPosture<PlayerEntity> {
    @Override
    public boolean applies(LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    @Override
    public void transform(IModel model, PlayerEntity player, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.setPitch((float) calculateIncline(player, motionX, motionY, motionZ));

        GlStateManager.rotatef(model.getPitch(), 1, 0, 0);

        float roll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        roll = model.getMetadata().getInterpolator(player.getUuid()).interpolate("pegasusRoll", roll, 10);

        GlStateManager.rotatef((float)roll, 0, 0, 1);
    }
}
