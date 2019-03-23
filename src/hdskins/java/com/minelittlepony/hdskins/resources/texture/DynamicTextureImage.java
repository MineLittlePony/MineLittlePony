package com.minelittlepony.hdskins.resources.texture;

import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.image.BufferedImage;

public class DynamicTextureImage extends DynamicTexture implements IBufferedTexture {

    private BufferedImage image;

    public DynamicTextureImage(BufferedImage bufferedImage) {
        super(bufferedImage);
        this.image = bufferedImage;
    }

    @Override
    public BufferedImage getBufferedImage() {
        return image;
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        this.image = null;
    }

}
