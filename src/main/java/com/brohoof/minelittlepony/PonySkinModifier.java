package com.brohoof.minelittlepony;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.voxelmodpack.hdskins.ISkinModifier;

public class PonySkinModifier implements ISkinModifier {

    @Override
    public void convertSkin(BufferedImage skin, Graphics dest) {
        int scale = skin.getWidth() / 64;
        drawImage(dest, skin, scale, 64, 32, 56, 48, 56, 16, 64, 32);
    }

    private void drawImage(Graphics graphics, Image image, int scale, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        graphics.drawImage(image,
                dx1 * scale, dy1 * scale, dx2 * scale, dy2 * scale,
                sx1 * scale, sy1 * scale, sx2 * scale, sy2 * scale,
                null);
    }
}
