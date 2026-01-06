package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.common.util.settings.Setting;

public class HorseCam {
    private static float lastOriginalPitch;
    private static float lastComputedPitch;

    private static final double TO_DEGREES = 180D / Math.PI;

    /**
     * Restores the previous camera (unadjusted) angle for the client when the server sends an update.
     * This is to prevent issues caused by the server updating our pitch whenever the player leaves a portal.
     */
    public static EntityPosition transformIncomingServerCameraAngle(EntityPosition change) {
        try {
            if (!PonyConfig.getInstance().fillycam.get()) {
                return change;
            }
            if (MathHelper.approximatelyEquals(change.pitch(), lastComputedPitch)) {
                return new EntityPosition(change.position(), change.deltaMovement(), change.yaw(), lastOriginalPitch);
            }
        } catch (Throwable t) {
            MineLittlePony.LOGGER.error("Error occured whilst handling player look {}", t);
        }
        return change;
    }

    /**
     * Transforms the client pony's pitch to the corresponding angle for a human character.
     */
    public static float transformCameraAngle(float pitch) {
        try {
            lastOriginalPitch = pitch;
            lastComputedPitch = pitch;

            if (!PonyConfig.getInstance().fillycam.get()) {
                return pitch;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            // noop
            // Only run when the player has an item in their hands. Can't check for buckets specifically since mods exist.
            if (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty()) {
                return pitch;
            }

            Pony pony = Pony.getManager().getPony(player);

            if (!pony.race().isHuman()) {
                Setting<Boolean> fillyCam = PonyConfig.getInstance().fillycam;

                fillyCam.set(false);
                final float vanillaHeight = player.getEyeHeight(player.getPose());
                fillyCam.set(true);
                final float alteredHeight = player.getEyeHeight(player.getPose());

                // only change the angle if required
                if (!MathHelper.approximatelyEquals(vanillaHeight, alteredHeight) && client.targetedEntity == null) {
                    // noop
                    // Ignore misses, helps with bows, arrows, and projectiles
                    if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                        pitch = rescaleCameraPitch(player, alteredHeight, vanillaHeight, pitch);
                    }
                }
            }

            lastComputedPitch = pitch;
            return pitch;
        } catch (Throwable t) {
            MineLittlePony.LOGGER.info("Error occured when unconverting camera pitch: {}", t);
        }
        return lastOriginalPitch;
    }

    /**
     * Calculates a corresponding camera pitch for the current player at
     * the specified character height.
     *
     * @param toHeight      Target height.
     * @param originalPitch Original, unchanged pitch.
     *
     * @return The new pitch value, otherwise the original value passed in.
     */
    public static float rescaleCameraPitch(Entity entity, double fromHeight, double toHeight, float originalPitch) {
        Vec3d start = entity.getEntityPos().add(0, fromHeight, 0);
        Vec3d end = getRaycastPos(entity, start, originalPitch);

        if (end == null) {
            return originalPitch;
        }

        return (float)adjustAngle(originalPitch, entity.getEntityPos(), end, start, fromHeight, toHeight);
    }

    public static @Nullable Vec3d getRaycastPos(Entity entity, Vec3d start, float pitch) {
        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        BlockHitResult hit = entity.getEntityWorld().raycast(new RaycastContext(
                start,
                start.add(entity.getRotationVector(pitch, entity.getYaw(tickDelta)).multiply(16)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, entity)
        );
        return hit == null ? null : hit.getPos();
    }

    public static double adjustAngle(double pitch, Vec3d origin, Vec3d end, Vec3d start, double fromHeight, double toHeight) {
        double x = horizontalDistance(start, end);
        double y = origin.y - end.y + toHeight;

        if (x == 0) {
            return pitch;
        }

        double newPitch = Math.atan(y / x) * TO_DEGREES;
        // Try not to break stuff
        if (Double.isInfinite(newPitch) || Double.isNaN(newPitch)) {
            return pitch;
        }

        return newPitch;
    }

    private static double horizontalDistance(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffZ = to.z - from.z;
        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }
}
