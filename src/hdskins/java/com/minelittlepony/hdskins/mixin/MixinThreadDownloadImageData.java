package com.minelittlepony.hdskins.mixin;

import net.minecraft.client.renderer.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.minelittlepony.hdskins.resources.texture.IBufferedTexture;

import java.awt.image.BufferedImage;

@Mixin(ThreadDownloadImageData.class)
public interface MixinThreadDownloadImageData extends IBufferedTexture {

    @Accessor("bufferedImage")
    @Override
    BufferedImage getBufferedImage();
}
