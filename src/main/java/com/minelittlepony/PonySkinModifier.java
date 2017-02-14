package com.minelittlepony;

import com.voxelmodpack.hdskins.ISkinModifier;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PonySkinModifier implements ISkinModifier {

    @Override
    public void convertSkin(BufferedImage skin, Graphics dest) {
        int scale = skin.getWidth() / 64;

        //top, mirror
        drawImage(dest, skin, scale, 60, 32, 58, 34, 58, 16, 60, 18);
        //bottom, mirror
        drawImage(dest, skin, scale, 62, 32, 60, 34, 60, 16, 62, 18);
        //inside
        drawImage(dest, skin, scale, 58, 34, 56, 48, 60, 18, 62, 32);
        //back
        drawImage(dest, skin, scale, 60, 34, 58, 48, 58, 18, 60, 32);
        //outside
        drawImage(dest, skin, scale, 62, 34, 60, 48, 56, 18, 58, 32);
        //back
        drawImage(dest, skin, scale, 64, 34, 62, 48, 62, 18, 64, 32);
    }

    private void drawImage(Graphics graphics, Image image, int scale, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        graphics.drawImage(image,
                dx1 * scale, dy1 * scale, dx2 * scale, dy2 * scale,
                sx1 * scale, sy1 * scale, sx2 * scale, sy2 * scale,
                null);
    }
}
