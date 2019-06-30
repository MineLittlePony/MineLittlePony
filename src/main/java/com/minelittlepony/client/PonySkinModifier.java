package com.minelittlepony.client;

import com.minelittlepony.hdskins.ISkinModifier;

public class PonySkinModifier implements ISkinModifier {

    @Override
    public void convertSkin(IDrawer drawer) {
       // copies the wing across for old legacy textures.

       // Scale factor so we can support texture sizes beyond 64x64
       int scale = drawer.getImage().getWidth() / 64;

                      // ( from ) ( to ) (size) flipX flipY
       drawer.draw(scale, 58, 16, 58, 32, 2,  2, true, false); // top
       drawer.draw(scale, 60, 16, 60, 32, 2,  2, true, false); // bottom
       drawer.draw(scale, 60, 18, 56, 34, 2, 14, true, false); // inside
       drawer.draw(scale, 58, 18, 58, 34, 2, 14, true, false); // back
       drawer.draw(scale, 56, 18, 60, 34, 2, 14, true, false); // outside
       drawer.draw(scale, 62, 18, 62, 34, 2, 14, true, false); // front
    }
}
