package com.minelittlepony.api.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;

import com.minelittlepony.util.MathUtil;

public interface QuadrupedLegPosing {
    /**
     * Rotates legs in quopy fashion for walking.
     */
    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void walk(T state,
            ModelPart frontLeftLeg, ModelPart frontRightLeg,
            ModelPart backLeftLeg, ModelPart backRightLeg
    ) {
        float angle = Mth.PI * (float) Math.pow(state.walkAnimationSpeed, 16);

        float baseRotation = state.walkAnimationPos * 0.6662F; // magic number ahoy
        float scale = state.walkAnimationSpeed * 0.25F;

        float rainboomLegLotation = state.getAttributes().getMainInterpolator().interpolate(
                "rainboom_leg_rotation",
                state.getAttributes().isGoingFast ? 1 : 0,
                5
        );
        float yAngle = 0.2F * rainboomLegLotation;

        frontLeftLeg.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + angle) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, 0);
        frontRightLeg.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + Mth.PI + angle / 2) * scale, -MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, 0);
        backLeftLeg.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + Mth.PI - (angle * 0.4f)) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), yAngle, backLeftLeg.zRot);
        backRightLeg.setRotation(Mth.lerp(rainboomLegLotation, Mth.cos(baseRotation + angle / 5) * scale, MathUtil.Angles._90_DEG * rainboomLegLotation), -yAngle, backRightLeg.zRot);
    }

    /**
     * Rotates legs in a quopy fashion whilst swimming.
     */
    static <T extends HumanoidRenderState & PonyModel.AttributedHolder> void swim(T state,
            ModelPart frontLeftLeg, ModelPart frontRightLeg,
            ModelPart backLeftLeg, ModelPart backRightLeg,
            float animationSpeed
    ) {
        float legLeft = (MathUtil.Angles._90_DEG + Mth.sin((state.walkAnimationPos / 3) + Mth.TWO_PI / 3) / 2) * animationSpeed;

        float left = (MathUtil.Angles._90_DEG + Mth.sin((state.walkAnimationPos / 3) + Mth.TWO_PI) / 2) * animationSpeed;
        float right = (MathUtil.Angles._90_DEG + Mth.sin(state.walkAnimationPos / 3) / 2) * animationSpeed;

        frontLeftLeg.setRotation(-left, -left / 2, left / 2);
        frontRightLeg.setRotation(-right, right / 2, -right / 2);
        backLeftLeg.setRotation(legLeft, 0, backLeftLeg.zRot);
        backRightLeg.setRotation(right, 0, backRightLeg.zRot);
    }
}
