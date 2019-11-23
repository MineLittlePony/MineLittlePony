package com.minelittlepony.client;

import com.minelittlepony.common.event.SkinFilterCallback;
import net.minecraft.client.texture.NativeImage;

import static com.minelittlepony.common.event.SkinFilterCallback.copy;

/**
 * Called to convert the legacy 64x32 skins to the new 64x64 format.
 *
 * Vanilla components are handle upstream, so all this really has to
 * do is copy the wing across.
 *
 */
class LegacySkinConverter implements SkinFilterCallback {

    @Override
    public void processImage(NativeImage image, boolean legacy) {
        if (legacy) {
            // ( from ) ( offset )  (size) flipX flipY
            copy(image, 58, 16, 0, 16, 2, 2, true, false); // top
            copy(image, 60, 16, 0, 16, 2, 2, true, false); // bottom
            copy(image, 60, 18, -4, 16, 2, 14, true, false); // inside
            copy(image, 58, 18, 0, 16, 2, 14, true, false); // back
            copy(image, 56, 18, 4, 16, 2, 14, true, false); // outside
            copy(image, 62, 18, 0, 16, 2, 14, true, false); // front
        }
    }
}
