package com.voxelmodpack.hdskins.mixin;

import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

@Mixin(ImageBufferDownload.class)
public abstract class MixinImageBufferDownload implements IImageBuffer {

    @Inject(
            method = "parseUserSkin(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;",
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(
                    value = "INVOKE",
                    shift = Shift.BEFORE,
                    target = "Ljava/awt/Graphics;dispose()V",
                    remap = false))
    private void update(BufferedImage image, CallbackInfo ci, BufferedImage image2) {
        // convert skins from mojang server
        boolean isLegacy = image.getHeight() == 32;
        Graphics graphics = image2.getGraphics();
        if (isLegacy) {
            HDSkinManager.INSTANCE.convertSkin(image2, graphics);
        }
    }

    // development
    @Surrogate
    private void update(BufferedImage image, CallbackInfo ci, BufferedImage image2, Graphics graphics, boolean isLegacy) {
        if (isLegacy) {
            HDSkinManager.INSTANCE.convertSkin(image2, graphics);
        }
    }

    // for optifine
    @Surrogate
    private void update(BufferedImage image, CallbackInfo ci, int w, int h, int k, BufferedImage image2, Graphics graphics, boolean isLegacy) {
        // convert skins from mojang server
        if (isLegacy) {
            HDSkinManager.INSTANCE.convertSkin(image2, graphics);
        }
    }

}
