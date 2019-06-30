package com.minelittlepony.client;

import com.minelittlepony.hdskins.ISkinModifier;

public class PonySkinModifier implements ISkinModifier {

    @Override
    public void convertSkin(ISkinModifier.IDrawer drawer) {
       int scale = drawer.getImage().getWidth() / 64;

       // TODO: What are these numbers!?

       drawer.draw(scale, 60, 32, 58, 34, 58, 16, 60, 18, false, false); // top, mirror
       drawer.draw(scale, 62, 32, 60, 34, 60, 16, 62, 18, false, false); // bottom, mirror
       drawer.draw(scale, 58, 34, 56, 48, 60, 18, 62, 32, false, false); // inside
       drawer.draw(scale, 60, 34, 58, 48, 58, 18, 60, 32, false, false); // back
       drawer.draw(scale, 62, 34, 60, 48, 56, 18, 58, 32, false, false); // outside
       drawer.draw(scale, 64, 34, 62, 48, 62, 18, 64, 32, false, false); // back
    }
}
