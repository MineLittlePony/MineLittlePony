package com.minelittlepony.hdskins.resources.texture;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.NativeImage;

@FunctionalInterface
public interface ISkinAvailableCallback extends IImageBuffer {
    @Override
    default NativeImage parseUserSkin(NativeImage image) {
        return image;
    }
}