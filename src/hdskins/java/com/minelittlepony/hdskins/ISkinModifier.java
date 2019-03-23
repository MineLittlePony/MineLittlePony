package com.minelittlepony.hdskins;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

@FunctionalInterface
public interface ISkinModifier {

    void convertSkin(BufferedImage skin, Graphics dest);
}
