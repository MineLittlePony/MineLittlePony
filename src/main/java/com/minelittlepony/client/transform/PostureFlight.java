package com.minelittlepony.client.transform;

import com.minelittlepony.common.util.animation.MotionCompositor;
import com.minelittlepony.model.IModel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;

public class PostureFlight extends MotionCompositor implements PonyPosture<PlayerEntity> {
    @Override
    public boolean applies(LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    @Override
    public void transform(IModel model, PlayerEntity player, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.getAttributes().motionPitch = (float) calculateIncline(player, motionX, motionY, motionZ);

        float roll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        roll = model.getMetadata().getInterpolator(player.getUuid()).interpolate("pegasusRoll", roll, 10);

        stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(model.getAttributes().motionPitch));
        stack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(roll));
    }
}
