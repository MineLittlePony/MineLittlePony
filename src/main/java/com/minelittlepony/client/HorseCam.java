package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.pony.IPony;

public class HorseCam {
    private static float lastOriginalPitch;
    private static float lastComputedPitch;

    /**
     * Restores the previous camera (unadjusted) angle for the client when the server sends an update.
     * This is to prevent issues caused by the server updating our pitch whenever the player leaves a portal.
     */
    public static float transformIncomingServerCameraAngle(float serverPitch) {
        if (MathHelper.approximatelyEquals(serverPitch, lastComputedPitch)) {
            return lastOriginalPitch;
        }
        return serverPitch;
    }

    /**
     * Transforms the client pony's pitch to the corresponding angle for a human character.
     */
    public static float transformCameraAngle(float pitch) {

        if (!MineLittlePony.getInstance().getConfig().fillycam.get()) {
            return pitch;
        }

        if (pitch != 0) {
            lastOriginalPitch = pitch;
            lastComputedPitch = pitch;
        }

        PlayerEntity player = MinecraftClient.getInstance().player;

        // noop
        // Only run when the player has an item in their hands. Can't check for buckets specifically since mods exist.
        if (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty()) {
            return pitch;
        }

        IPony pony = MineLittlePony.getInstance().getManager().getPony(player);

        if (!pony.getRace().isHuman()) {
            float factor = pony.getMetadata().getSize().getEyeHeightFactor();
            pitch = rescaleCameraPitch(player.getStandingEyeHeight() / factor, pitch);
        }

        if (lastOriginalPitch != 0) {
            lastComputedPitch = pitch;
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
    public static float rescaleCameraPitch(double toHeight, float originalPitch) {
        /*     -90
         *     |
         *     ---------------0
         *     |
         *     90
         */
        /*  A                A - toHeight
         *  |\               B - fromHeight
         *  |?\              y - headPitch
         *  |  \             ? - result
         *  |   \            C - raytrace
         *  B-   \
         *  |y -  \
         *  |    - \         Tan(?) = horDist / toHeight
         *==|-------C===         ?  = arcTan(horDist / toHeight);
         *   horDist
         *
         *   horDist
         *  |-------C
         *  |      /.
         *  |     /.
         *  |    / .
         *  |   / .
         *  |  /  .
         *  |?/  .
         *  A/  .
         *  |   .
         *  |  .
         *  |  .
         *  | .
         *  B
         *  |
         *  |
         *==o===========
         */

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        client.gameRenderer.updateTargetedEntity(client.getTickDelta());
        HitResult hit = client.crosshairTarget;

        if (client.targetedEntity != null) {
            return originalPitch;
        }

        // noop
        // Ignore misses, helps with bows, arrows, and projectiles
        if (hit == null || hit.getType() != HitResult.Type.BLOCK || player == null) {
            return originalPitch;
        }

        Vec3d hitPos = hit.getPos();
        Vec3d pos = player.getPos();

        double diffX = Math.abs(hitPos.x - pos.x);
        double diffZ = Math.abs(hitPos.z - pos.z);

        double horDist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        double toEyePos = pos.y + toHeight;

        double verDist = Math.abs(hitPos.y - toEyePos);

        double theta = Math.atan(horDist / verDist);

        // convert to degress
        theta /= Math.PI / 180D;

        // convert to vertical pitch (-90 to 90).
        // Preserve up/down direction.
        double newPitch = Math.abs(90 - theta) * Math.signum(originalPitch);

        return (float)newPitch;
    }
}
