package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.api.pony.IPony;

public class HorseCam {
    /**
     * Transforms the client pony's pitch to the corresponding angle for a human character.
     */
    public static float transformCameraAngle(float pitch) {

        if (!MineLittlePony.getInstance().getConfig().fillycam.get()) {
            return pitch;
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        IPony pony = MineLittlePony.getInstance().getManager().getPony(player);

        if (!pony.getRace(false).isHuman()) {
            float factor = pony.getMetadata().getSize().getEyeHeightFactor();
            return rescaleCameraPitch(player.getStandingEyeHeight() / factor, pitch);
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
        client.gameRenderer.updateTargetedEntity(1);
        HitResult hit = client.crosshairTarget;

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
