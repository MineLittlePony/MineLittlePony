package com.minelittlepony.client.transform;

import net.minecraft.world.entity.LivingEntity;

import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.animation.MotionCompositor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;


public class PostureFlight extends PonyPosture {
    private final MotionCompositor compositor = new MotionCompositor();

    private final float xScale;
    private final float yOffset;

    public PostureFlight(float xScale, float yOffset) {
        this.xScale = xScale;
        this.yOffset = yOffset;
    }

    @Override
    public void updateState(LivingEntity entity, PonyRenderState state) {
        super.updateState(entity, state);

        double motionX = entity.getX() - entity.xo;
        double motionY = entity.onGround() ? 0 : entity.getY() - entity.yo;
        double motionZ = entity.getZ() - entity.zo;

        state.attributes.motionPitch = (float)compositor.calculateIncline(entity, motionX, motionY, motionZ);
        state.attributes.motionRoll = (float)compositor.calculateRoll(entity, motionX * xScale,  motionY, motionZ * xScale);
        state.attributes.motionRoll = state.attributes.getMainInterpolator().interpolate("pegasusRoll", state.attributes.motionRoll, 10);
    }

    @Override
    public void transform(PonyRenderState state, PoseStack stack) {
        stack.mulPose(Axis.XP.rotationDegrees(state.attributes.motionPitch));
        stack.mulPose(Axis.ZP.rotationDegrees(state.attributes.motionRoll));
        stack.translate(0, yOffset, 0);
    }
}
