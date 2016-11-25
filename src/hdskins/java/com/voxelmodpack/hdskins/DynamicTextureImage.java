package com.voxelmodpack.hdskins;

import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.image.BufferedImage;

public class DynamicTextureImage extends DynamicTexture {

    private BufferedImage image;

    public DynamicTextureImage(BufferedImage bufferedImage) {
        super(bufferedImage);
        this.image = bufferedImage;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        this.image = null;
    }

}
