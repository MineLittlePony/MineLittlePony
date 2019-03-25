package com.minelittlepony.hdskins.resources.texture;

import net.minecraft.client.renderer.texture.NativeImage;

import javax.annotation.Nullable;

public interface IBufferedTexture {

    @Nullable
    NativeImage getBufferedImage();
}
