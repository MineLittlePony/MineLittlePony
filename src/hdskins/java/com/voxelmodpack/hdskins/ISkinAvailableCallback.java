package com.voxelmodpack.hdskins;

import net.minecraft.client.renderer.IImageBuffer;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface ISkinAvailableCallback extends IImageBuffer {
    default BufferedImage parseUserSkin(BufferedImage image) {
        return image;
    }
}