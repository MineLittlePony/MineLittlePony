package com.minelittlepony.mixin;

import java.awt.image.BufferedImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.ThreadDownloadImageData;

@Mixin(ThreadDownloadImageData.class)
public interface MixinThreadDownloadImageData {
    @Accessor("bufferedImage")
    BufferedImage getBufferedImage();
}
