package com.brohoof.minelittlepony;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.voxelmodpack.hdskins.ISkinModifier;

public class PonySkinModifier implements ISkinModifier {

    @Override
    public void convertSkin(BufferedImage skin, Graphics dest) {
        int scale = skin.getWidth() / 64;

        //top, mirror
        drawImage(dest, skin, scale, 60, 34, 58, 32, 58, 18, 60, 16);
        //bottom, mirror
        drawImage(dest, skin, scale, 62, 34, 60, 32, 60, 18, 62, 16);
        //inside
        drawImage(dest, skin, scale, 56, 34, 58, 48, 60, 18, 62, 32);
        //front, mirror
        drawImage(dest, skin, scale, 60, 48, 58, 34, 58, 32, 60, 18);
        //outside
        drawImage(dest, skin, scale, 60, 34, 62, 48, 56, 18, 58, 32);
        //back
        drawImage(dest, skin, scale, 62, 34, 64, 48, 62, 18, 64, 32);
    }

    private void drawImage(Graphics graphics, Image image, int scale, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        graphics.drawImage(image,
                dx1 * scale, dy1 * scale, dx2 * scale, dy2 * scale,
                sx1 * scale, sy1 * scale, sx2 * scale, sy2 * scale,
                null);
    }
}
