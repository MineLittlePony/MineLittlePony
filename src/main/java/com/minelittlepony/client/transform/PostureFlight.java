package com.minelittlepony.client.transform;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.common.util.animation.MotionCompositor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.LivingEntity;

public class PostureFlight extends MotionCompositor implements PonyPosture<PlayerEntity> {
    @Override
    public boolean applies(LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    @Override
    public void transform(IModel model, PlayerEntity player, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.getAttributes().motionPitch = (float) calculateIncline(player, motionX, motionY, motionZ);
        model.getAttributes().motionRoll = (float)calculateRoll(player, motionX,  motionY, motionZ);

        model.getAttributes().motionRoll = model.getMetadata().getInterpolator(model.getAttributes().interpolatorId).interpolate("pegasusRoll", model.getAttributes().motionRoll, 10);

        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(model.getAttributes().motionPitch));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(model.getAttributes().motionRoll));
    }
}
