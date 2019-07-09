package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import com.minelittlepony.pony.IPony;
import com.minelittlepony.settings.PonySettings;

public class HorseCam {
    /**
     * Transforms the client pony's pitch to the corresponding angle for a human character.
     */
    public static float transformCameraAngle(float pitch) {

        if (!PonySettings.FILLYCAM.get()) {
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
         *  |-------C            ?  = arcTan(horDist / toHeight);
         *   horDist
         */
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        HitResult hit = client.hitResult;

        // noop
        if (hit == null || player == null) {
            return originalPitch;
        }

        // Small angles aren't worth changing.
        // Helps with bows, arrows, and projectiles.
        if (Math.abs(originalPitch) < 10) {
            return originalPitch;
        }

        Vec3d hitPos = hit.getPos();
        Vec3d pos = player.getPos();

        double diffX = Math.abs(hitPos.x - pos.x);
        double diffZ = Math.abs(hitPos.z - pos.z);
        double horDist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float theta = (float)Math.atan(horDist / toHeight);

        // convert to degress
        theta /= Math.PI / 180;

        // convert to vertical pitch (-90 to 90).
        // Preserve up/down direction.
        float newPitch = (90 - theta) * Math.signum(originalPitch);

        System.out.println("From " + originalPitch + " to " + newPitch);

        return newPitch;
    }
}
