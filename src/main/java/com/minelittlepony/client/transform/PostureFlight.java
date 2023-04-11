package com.minelittlepony.client.transform;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.common.util.animation.MotionCompositor;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import net.minecraft.entity.LivingEntity;

public class PostureFlight extends MotionCompositor implements PonyPosture {

    private final float xScale;
    private final float yOffset;

    public PostureFlight(float xScale, float yOffset) {
        this.xScale = xScale;
        this.yOffset = yOffset;
    }

    @Override
    public void transform(IModel model, LivingEntity player, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float ticks) {
        model.getAttributes().motionPitch = (float)calculateIncline(player, motionX, motionY, motionZ);
        model.getAttributes().motionRoll = (float)calculateRoll(player, motionX * xScale,  motionY, motionZ * xScale);

        model.getAttributes().motionRoll = model.getAttributes().getMainInterpolator().interpolate("pegasusRoll", model.getAttributes().motionRoll, 10);

        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(model.getAttributes().motionPitch));
        stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(model.getAttributes().motionRoll));
        stack.translate(0, yOffset, 0);
    }
}
