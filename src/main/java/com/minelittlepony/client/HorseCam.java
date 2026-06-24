package com.minelittlepony.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

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

            Minecraft client = Minecraft.getInstance();
            Player player = client.player;

            if (player == null || client.isLocalServer() || client.hasSingleplayerServer()) {
                return pitch;
            }

            // noop
            // Only run when the player has an item in their hands. Can't check for buckets specifically since mods exist.
            if (player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) {
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
                if (!Mth.equal(vanillaHeight, alteredHeight) && client.crosshairPickEntity == null) {
                    // noop
                    // Ignore misses, helps with bows, arrows, and projectiles
                    if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.BLOCK) {
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
        Vec3 start = entity.getPosition(tickProgress).add(0, fromHeight, 0);
        Vec3 end = getRaycastPos(entity, start, originalPitch, tickProgress);

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

    public static @Nullable Vec3 getRaycastPos(Entity entity, Vec3 start, float pitch, float tickProgress) {
        BlockHitResult hit = entity.level().clip(new ClipContext(
                start,
                start.add(entity.calculateViewVector(pitch, entity.getYRot(tickProgress)).scale(16)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)
        );
        return hit == null ? null : hit.getLocation();
    }

    private static double horizontalDistance(Vec3 from, Vec3 to) {
        double diffX = to.x - from.x;
        double diffZ = to.z - from.z;
        return Math.sqrt(diffX * diffX + diffZ * diffZ);
    }
}
