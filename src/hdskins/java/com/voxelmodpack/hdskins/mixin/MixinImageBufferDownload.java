package com.voxelmodpack.hdskins.mixin;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;

@Mixin(ImageBufferDownload.class)
public abstract class MixinImageBufferDownload implements IImageBuffer {

    @Inject(
            method = "parseUserSkin",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    shift = Shift.BEFORE,
                    target = "Ljava/awt/Graphics;dispose()V",
                    remap = false))
    private void update(BufferedImage image, CallbackInfo ci, BufferedImage image2, Graphics graphics) {
        // convert skins from mojang server
        if (image.getHeight() == 32) {
            HDSkinManager.INSTANCE.convertSkin(image2, graphics);
        }
    }

    // for optifine
    @Surrogate
    private void update(BufferedImage image, CallbackInfo ci, int w, int h, int k, BufferedImage image2, Graphics graphics) {
        // convert skins from mojang server
        if (image.getHeight() == 32) {
            HDSkinManager.INSTANCE.convertSkin(image2, graphics);
        }
    }

}
