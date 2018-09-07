package com.voxelmodpack.hdskins.resources.texture;

import net.minecraft.client.renderer.IImageBuffer;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface ISkinAvailableCallback extends IImageBuffer {
    @Override
    default BufferedImage parseUserSkin(BufferedImage image) {
        return image;
    }
}