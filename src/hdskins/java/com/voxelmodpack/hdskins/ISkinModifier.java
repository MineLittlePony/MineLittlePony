package com.voxelmodpack.hdskins;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Interface for mods to convert player skins before loading.
 */
@FunctionalInterface
public interface ISkinModifier {
    /**
     * Called to convert the given skin.
     * 
     * @param skin  The skin being converted
     * @param dest  Graphics for drawing onto the image
     */
    void convertSkin(BufferedImage skin, Graphics dest);
}
