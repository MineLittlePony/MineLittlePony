package com.minelittlepony.client.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.minelittlepony.client.ducks.IBufferedTexture;

@Mixin(PlayerSkinTexture.class)
public abstract class MixinThreadDownloadImageData extends ResourceTexture implements IBufferedTexture {

    MixinThreadDownloadImageData() {super(null);}

    private NativeImage cachedImage;

    @Override
    public NativeImage getBufferedImage() {
        return cachedImage;
    }

    @Inject(method = "method_4534("
            + "Lnet/minecraft/client/texture/NativeImage;)V",
            at = @At("HEAD"))
    private void onSetImage(NativeImage nativeImageIn) {
        cachedImage = nativeImageIn;
    }
}
