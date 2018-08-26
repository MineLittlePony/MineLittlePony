package com.minelittlepony.mixin;

import com.voxelmodpack.hdskins.IBufferedTexture;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.awt.image.BufferedImage;

@Mixin(ThreadDownloadImageData.class)
public interface MixinThreadDownloadImageData extends IBufferedTexture {

    @Accessor("bufferedImage")
    @Override
    BufferedImage getBufferedImage();
}
