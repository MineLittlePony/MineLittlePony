package com.minelittlepony.client.transform;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;

import org.jetbrains.annotations.NotNull;

import com.minelittlepony.api.model.*;

public interface PonyPosture<T extends LivingEntity> {
    PonyPosture<LivingEntity> STANDING = (IModel model, LivingEntity entity, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float tickDelta) -> {
        model.getAttributes().motionPitch /= 10;
        model.getAttributes().motionLerp /= 10;
        model.getAttributes().motionRoll /= 10;
    };
    PonyPosture<LivingEntity> ELYTRA = (IModel model, LivingEntity entity, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float tickDelta) -> {
        stack.translate(0, entity.isInSneakingPose() ? -0.825F : -1, 0);
    };
    PonyPosture<LivingEntity> FLYING = new PostureFlight(1, 0);
    PonyPosture<LivingEntity> SWIMMING = new PostureFlight(2, -0.9F);
    PonyPosture<LivingEntity> FALLING = STANDING;

    @NotNull
    static PonyPosture<?> getPosture(ModelAttributes attributes, LivingEntity entity) {
        if (attributes.isGliding) {
            return ELYTRA;
        }

        if (attributes.isSleeping) {
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

    default void apply(T player, IModel model, MatrixStack stack, float yaw, float tickDelta, int invert) {

        if (RenderPass.getCurrent() == RenderPass.GUI || RenderPass.getCurrent() == RenderPass.WORLD) {
            // this reverts the rotations done in PlayerEntityRenderer#setupTransforms
            if (player instanceof PlayerEntity) {
                float leaningPitch = player.getLeaningPitch(tickDelta);
                if (player.isFallFlying()) {

                    if (RenderPass.getCurrent() == RenderPass.GUI) {
                        Vec3d vec3d = player.getRotationVec(tickDelta);
                        Vec3d vec3d2 = ((AbstractClientPlayerEntity)player).lerpVelocity(tickDelta);
                        double d = vec3d2.horizontalLengthSquared();
                        double e = vec3d.horizontalLengthSquared();
                        if (d > 0.0 && e > 0.0) {
                            double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                            double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                            stack.multiply(RotationAxis.NEGATIVE_Y.rotation((float)(Math.signum(m) * Math.acos(l))));
                        }
                    }

                    float roll = (float)player.getRoll() + tickDelta;
                    float targetRoll = MathHelper.clamp(roll * roll / 100F, 0, 1);
                    if (!player.isUsingRiptide()) {
                        stack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(targetRoll * (-90 - player.getPitch())));
                    }

                } else if (leaningPitch > 0) {
                    if (player.isInSwimmingPose()) {
                        stack.translate(0.0f, 1.0f, -0.3f);
                    }
                    float pitch = MathHelper.lerp(leaningPitch, 0, player.isTouchingWater() ? -90 - player.getPitch() : -90);
                    stack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(pitch));
                }
            }
        }

        if (RenderPass.getCurrent() != RenderPass.WORLD) {
            return;
        }

        double motionX = player.getX() - player.prevX;
        double motionY = player.isOnGround() ? 0 : player.getY() - player.prevY;
        double motionZ = player.getZ() - player.prevZ;

        transform(model, player, stack, motionX, invert * motionY, motionZ, yaw, tickDelta);
    }

    void transform(IModel model, T entity, MatrixStack stack, double motionX, double motionY, double motionZ, float yaw, float tickDelta);
}
