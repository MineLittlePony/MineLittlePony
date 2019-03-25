package com.minelittlepony.hdskins.mixin;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.NativeImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.hdskins.HDSkinManager;
import com.minelittlepony.hdskins.resources.texture.SimpleDrawer;

@Mixin(ImageBufferDownload.class)
public abstract class MixinImageBufferDownload implements IImageBuffer {

    @Inject(
            method = "parseUserSkin(Lnet/minecraft/client/renderer/texture/NativeImage;)Lnet/minecraft/client/renderer/texture/NativeImage;",
            at = @At("RETURN"),
            cancellable = true)
    private void update(NativeImage image, CallbackInfoReturnable<NativeImage> ci) {
        // convert skins from mojang server
        NativeImage image2 = ci.getReturnValue();
        boolean isLegacy = image.getHeight() == 32;
        if (isLegacy) {
            HDSkinManager.INSTANCE.convertSkin((SimpleDrawer)(() -> image2));
        }
    }
}
