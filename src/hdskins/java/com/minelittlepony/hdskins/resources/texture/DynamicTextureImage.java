package com.minelittlepony.hdskins.resources.texture;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;

public class DynamicTextureImage extends DynamicTexture implements IBufferedTexture {

    private NativeImage image;

    public DynamicTextureImage(NativeImage bufferedImage) {
        super(bufferedImage);
        this.image = bufferedImage;
    }

    @Override
    public NativeImage getBufferedImage() {
        return image;
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        this.image = null;
    }

}
