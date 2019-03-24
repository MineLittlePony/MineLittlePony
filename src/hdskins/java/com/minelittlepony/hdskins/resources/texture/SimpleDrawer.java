package com.minelittlepony.hdskins.resources.texture;

import com.minelittlepony.hdskins.ISkinModifier;

public interface SimpleDrawer extends ISkinModifier.IDrawer {

    @Override
    default void draw(int scale,
            /*destination: */ int dx1, int dy1, int dx2, int dy2,
            /*source: */      int sx1, int sy1, int sx2, int sy2) {

        int srcX = sx1 * scale;
        int srcY = sy1 * scale;

        int dstX = dx1 * scale;
        int dstY = dy1 * scale;

        int width = (sx2 - sx1) * scale;
        int height = (sy2 - sy1) * scale;

        getImage().copyAreaRGBA(srcX, srcY, dstX, dstY, width, height, false, false);
    }

    /*public void copyAreaRGBA(int srcX, int srcY, int dstX, int dstY, int width, int height, boolean flipX, boolean flipY) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

                int shiftX = flipX ? width - 1 - x : x;
                int shiftY = flipY ? height - 1 - y : y;

                int value = image.getPixelRGBA(srcX + x, srcY + y);

                image.setPixelRGBA(
                        srcX + dstX + shiftX,
                        srcY + dstY + shiftY,
                        value
                );
            }
        }
    }*/
}
