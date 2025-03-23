package com.minelittlepony.client.transform;

import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.animation.MotionCompositor;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.LivingEntity;

public class PostureFlight extends PonyPosture {
    private final MotionCompositor compositor = new MotionCompositor();

    private final float xScale;
    private final float yOffset;

    public PostureFlight(float xScale, float yOffset) {
        this.xScale = xScale;
        this.yOffset = yOffset;
    }

    public void updateState(LivingEntity entity, PonyRenderState state) {
        super.updateState(entity, state);

        double motionX = entity.getX() - entity.lastX;
        double motionY = entity.isOnGround() ? 0 : entity.getY() - entity.lastY;
        double motionZ = entity.getZ() - entity.lastZ;

        state.attributes.motionPitch = (float)compositor.calculateIncline(entity, motionX, motionY, motionZ);
        state.attributes.motionRoll = (float)compositor.calculateRoll(entity, motionX * xScale,  motionY, motionZ * xScale);
        state.attributes.motionRoll = state.attributes.getMainInterpolator().interpolate("pegasusRoll", state.attributes.motionRoll, 10);
    }

    @Override
    public void transform(PonyRenderState state, MatrixStack stack) {
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.attributes.motionPitch));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.attributes.motionRoll));
        stack.translate(0, yOffset, 0);
    }
}
