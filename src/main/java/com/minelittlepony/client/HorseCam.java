package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.common.util.settings.Setting;

public class HorseCam {
    private static float lastOriginalPitch;
    private static float lastComputedPitch;

    private static final double HALF_PI = Math.PI / 2D;
    private static final double TO_DEGREES = 180D / Math.PI;

    /**
     * Restores the previous camera (unadjusted) angle for the client when the server sends an update.
     * This is to prevent issues caused by the server updating our pitch whenever the player leaves a portal.
     */
    public static PlayerPosition transformIncomingServerCameraAngle(PlayerPosition change) {
        try {
            if (!PonyConfig.getInstance().fillycam.get()) {
                return change;
            }
            if (MathHelper.approximatelyEquals(change.pitch(), lastComputedPitch)) {
                return new PlayerPosition(change.position(), change.deltaMovement(), change.yaw(), lastOriginalPitch);
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

            PlayerEntity player = MinecraftClient.getInstance().player;

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
                if (!MathHelper.approximatelyEquals(vanillaHeight, alteredHeight)) {
                    pitch = rescaleCameraPitch(vanillaHeight, pitch);
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
    public static float rescaleCameraPitch(double toHeight, float originalPitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        client.gameRenderer.updateCrosshairTarget(client.getRenderTickCounter().getTickDelta(false));
        HitResult hit = client.crosshairTarget;

        if (client.targetedEntity != null) {
            return originalPitch;
        }

        // noop
        // Ignore misses, helps with bows, arrows, and projectiles
        if (hit == null || hit.getType() != HitResult.Type.BLOCK || player == null) {
            return originalPitch;
        }

        return (float)adjustAngle(originalPitch, hit.getPos(), player.getPos(), toHeight);
    }

    private static double adjustAngle(double pitch, Vec3d hitPos, Vec3d pos, double toHeight) {
        double x = horizontalDistance(pos, hitPos);
        double y = pos.y + toHeight - hitPos.y;

        if (MathHelper.approximatelyEquals(y, 0)) {
            return 0;
        }

        double newPitch = (HALF_PI - Math.atan(x / y)) * TO_DEGREES;
        // Try not to break stuff
        if (Double.isInfinite(newPitch) || Double.isNaN(newPitch)) {
            return pitch;
        }

        if (newPitch > 90) {
            newPitch -= 180F;
        }

        return newPitch;
    }

    private static double horizontalDistance(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffZ = to.z - from.z;
        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }
}
