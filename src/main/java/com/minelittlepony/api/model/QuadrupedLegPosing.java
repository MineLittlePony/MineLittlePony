package com.minelittlepony.api.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.util.MathUtil;

public interface QuadrupedLegPosing {
    /**
     * Rotates legs in quopy fashion for walking.
     */
    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void walk(T state,
            ModelPart frontLeftLeg, ModelPart frontRightLeg,
            ModelPart backLeftLeg, ModelPart backRightLeg
    ) {
        float angle = MathHelper.PI * (float) Math.pow(state.limbSwingAmplitude, 16);

        float baseRotation = state.limbSwingAnimationProgress * 0.6662F; // magic number ahoy
        float scale = state.limbSwingAmplitude * 0.25F;

        float rainboomLegLotation = state.getAttributes().getMainInterpolator().interpolate(
                "rainboom_leg_rotation",
                state.getAttributes().isGoingFast ? 1 : 0,
                5
        );
        float yAngle = 0.2F * rainboomLegLotation;

        frontLeftLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + angle) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, 0);
        frontRightLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + MathHelper.PI + angle / 2) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, 0);
        backLeftLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + MathHelper.PI - (angle * 0.4f)) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, backLeftLeg.roll);
        backRightLeg.setAngles(MathHelper.lerp(rainboomLegLotation, MathHelper.cos(baseRotation + angle / 5) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, backRightLeg.roll);
    }

    /**
     * Rotates legs in a quopy fashion whilst swimming.
     */
    static <T extends BipedEntityRenderState & PonyModel.AttributedHolder> void swim(T state,
            ModelPart frontLeftLeg, ModelPart frontRightLeg,
            ModelPart backLeftLeg, ModelPart backRightLeg,
            float animationSpeed
    ) {
        float legLeft = (MathUtil.Angles._90_DEG + MathHelper.sin((state.limbSwingAnimationProgress / 3) + MathHelper.TAU / 3) / 2) * animationSpeed;

        float left = (MathUtil.Angles._90_DEG + MathHelper.sin((state.limbSwingAnimationProgress / 3) + MathHelper.TAU) / 2) * animationSpeed;
        float right = (MathUtil.Angles._90_DEG + MathHelper.sin(state.limbSwingAnimationProgress / 3) / 2) * animationSpeed;

        frontLeftLeg.setAngles(-left, -left / 2, left / 2);
        frontRightLeg.setAngles(-right, right / 2, -right / 2);
        backLeftLeg.setAngles(legLeft, 0, backLeftLeg.roll);
        backRightLeg.setAngles(right, 0, backRightLeg.roll);
    }
}
