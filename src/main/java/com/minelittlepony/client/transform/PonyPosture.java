package com.minelittlepony.client.transform;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public abstract class PonyPosture {
    public static final PonyPosture STANDING = new PonyPosture() {
        @Override
        public void updateState(LivingEntity entity, PonyRenderState state) {
            super.updateState(entity, state);
            state.attributes.motionPitch /= 10;
            state.attributes.motionLerp /= 10;
            state.attributes.motionRoll /= 10;
        }
    };
    public static final PonyPosture ELYTRA = new PonyPosture() {
        @Override
        public void transform(PonyRenderState state, MatrixStack stack) {
            stack.translate(0, state.isInSneakingPose ? -0.825F : -1, 0);
        }
    };
    public static final PonyPosture FLYING = new PostureFlight(1, 0);
    public static final PonyPosture SWIMMING = new PostureFlight(2, -0.9F);
    public static final PonyPosture FALLING = STANDING;

    @NotNull
    public static PonyPosture of(ModelAttributes attributes) {
        if (attributes.isGliding) {
            return ELYTRA;
        }

        if (attributes.isLyingDown) {
            return STANDING;
        }

        if (attributes.isSwimming) {
            return SWIMMING;
        }

        if (attributes.isGoingFast && !attributes.isRiptide) {
            return FLYING;
        }

        return FALLING;
    }

    public void updateState(LivingEntity entity, PonyRenderState state) {
        if (RenderPass.getCurrent() == RenderPass.GUI || RenderPass.getCurrent() == RenderPass.WORLD) {
            if (entity instanceof AbstractClientPlayerEntity) {
                state.isGliding = false;
                state.leaningPitch = 0;
            }
        }
    }

    public void transform(PonyRenderState state, MatrixStack stack) {

    }
}
