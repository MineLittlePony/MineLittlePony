package com.voxelmodpack.hdskins.mixin;

import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

@Mixin(ImageBufferDownload.class)
public abstract class MixinImageBufferDownload implements IImageBuffer {

    @Inject(
            method = "parseUserSkin(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;",
            at = @At("RETURN"),
    cancellable = true)
    private void update(BufferedImage image, CallbackInfoReturnable<BufferedImage> ci) {
        // convert skins from mojang server
        BufferedImage image2 = ci.getReturnValue();
        boolean isLegacy = image.getHeight() == 32;
        if (isLegacy) {
            Graphics graphics = image2.getGraphics();
            HDSkinManager.INSTANCE.convertSkin(image2, graphics);
            graphics.dispose();
        }
    }

}
