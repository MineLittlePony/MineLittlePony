package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
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
    private static final double TO_DEGREES = 180D / Math.PI;

    /**
     * Transforms the client pony's pitch to the corresponding angle for a human character.
     */
    public static float transformCameraAngle(float pitch) {
        try {
            if (!PonyConfig.getInstance().fillycam.get() || PonyConfig.getInstance().disablebucketfix.get()) {
                return pitch;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player == null || client.isInSingleplayer() || client.isIntegratedServerRunning()) {
                return pitch;
            }

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
                        return rescaleCameraPitch(player, alteredHeight, vanillaHeight, pitch, 1);
                    }
                }
            }
        } catch (Throwable t) {
            MineLittlePony.LOGGER.info("Error occured when unconverting camera pitch: {}", t);
        }
        return pitch;
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
    public static float rescaleCameraPitch(Entity entity, double fromHeight, double toHeight, float originalPitch, float tickProgress) {
        Vec3d start = entity.getLerpedPos(tickProgress).add(0, fromHeight, 0);
        Vec3d end = getRaycastPos(entity, start, originalPitch, tickProgress);

        if (end == null) {
            return originalPitch;
        }

        double x = horizontalDistance(start, end);
        double y = entity.getY() - end.y + toHeight;

        if (x == 0) {
            return originalPitch;
        }

        double newPitch = Math.atan(y / x) * TO_DEGREES;
        // Try not to break stuff
        if (Double.isInfinite(newPitch) || Double.isNaN(newPitch)) {
            return originalPitch;
        }

        return (float)newPitch;
    }

    public static @Nullable Vec3d getRaycastPos(Entity entity, Vec3d start, float pitch, float tickProgress) {
        BlockHitResult hit = entity.getEntityWorld().raycast(new RaycastContext(
                start,
                start.add(entity.getRotationVector(pitch, entity.getYaw(tickProgress)).multiply(16)),
                RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity)
        );
        return hit == null ? null : hit.getPos();
    }

    private static double horizontalDistance(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffZ = to.z - from.z;
        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }
}
