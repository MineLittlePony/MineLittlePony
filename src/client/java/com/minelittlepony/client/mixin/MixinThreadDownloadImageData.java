package com.minelittlepony.client.mixin;

import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.minelittlepony.client.ducks.IBufferedTexture;

@Mixin(ThreadDownloadImageData.class)
public abstract class MixinThreadDownloadImageData extends SimpleTexture implements IBufferedTexture {

    MixinThreadDownloadImageData() {super(null);}

    private NativeImage cachedImage;
    
    @Override
    public NativeImage getBufferedImage() {
        return cachedImage;
    }

    @Inject(method = "setImage(Lnet/minecraft/client/renderer/texture/NativeImage;)V",
            at = @At("HEAD"))
    private void onSetImage(NativeImage nativeImageIn) {
        cachedImage = nativeImageIn;
    }
}
