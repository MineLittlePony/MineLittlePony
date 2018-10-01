package com.minelittlepony.transform;

import com.minelittlepony.model.AbstractPonyModel;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class PostureFlight extends MotionCompositor implements PonyPosture<AbstractClientPlayer> {
    @Override
    public boolean applies(EntityLivingBase entity) {
        return entity instanceof AbstractClientPlayer;
    }

    @Override
    public void transform(AbstractPonyModel model, AbstractClientPlayer player, double motionX, double motionY, double motionZ, float pitch, float yaw, float ticks) {
        model.motionPitch = (float) calculateIncline(player, motionX, motionY, motionZ);

        GlStateManager.rotate(model.motionPitch, 1, 0, 0);

        float roll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        roll = model.getMetadata().getInterpolator().interpolate("pegasusRoll", roll, 10);

        GlStateManager.rotate((float)roll, 0, 0, 1);
    }
}
