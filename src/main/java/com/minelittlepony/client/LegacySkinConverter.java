package com.minelittlepony.client;

import com.minelittlepony.common.util.TextureConverter;

/**
 * Called to convert the legacy 64x32 skins to the new 64x64 format.
 *
 * Vanilla components are handle upstream, so all this really has to
 * do is copy the wing across.
 *
 */
public class LegacySkinConverter implements TextureConverter {

    @Override
    public void convertTexture(Drawer drawer) {
              // ( from ) ( to )  (size) flipX flipY
       drawer.copy(58, 16, 58, 32, 2,  2, true, false); // top
       drawer.copy(60, 16, 60, 32, 2,  2, true, false); // bottom
       drawer.copy(60, 18, 56, 34, 2, 14, true, false); // inside
       drawer.copy(58, 18, 58, 34, 2, 14, true, false); // back
       drawer.copy(56, 18, 60, 34, 2, 14, true, false); // outside
       drawer.copy(62, 18, 62, 34, 2, 14, true, false); // front
    }
}
