package com.minelittlepony.client.transform;

import com.minelittlepony.client.model.IClientModel;
import com.minelittlepony.util.transform.MotionCompositor;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class PostureFlight extends MotionCompositor implements PonyPosture<AbstractClientPlayer> {
    @Override
    public boolean applies(EntityLivingBase entity) {
        return entity instanceof AbstractClientPlayer;
    }

    @Override
    public void transform(IClientModel model, AbstractClientPlayer player, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.setPitch((float) calculateIncline(player, motionX, motionY, motionZ));

        GlStateManager.rotate(model.getPitch(), 1, 0, 0);

        float roll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        roll = model.getMetadata().getInterpolator(player.getUniqueID()).interpolate("pegasusRoll", roll, 10);

        GlStateManager.rotate((float)roll, 0, 0, 1);
    }
}
