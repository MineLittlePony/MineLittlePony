package com.minelittlepony.hdskins;

import net.minecraft.client.renderer.texture.NativeImage;

@FunctionalInterface
public interface ISkinModifier {

    void convertSkin(IDrawer drawer);

    interface IDrawer {
        NativeImage getImage();

        void draw(int scale,
                /*destination: */ int dx1, int dy1, int dx2, int dy2,
                /*source: */      int sx1, int sy1, int sx2, int sy2);
    }
}
