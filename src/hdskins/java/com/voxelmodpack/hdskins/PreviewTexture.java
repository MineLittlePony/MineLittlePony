package com.voxelmodpack.hdskins;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public class PreviewTexture extends ThreadDownloadImageData {

    private boolean uploaded;

    public PreviewTexture(String url, ResourceLocation fallbackTexture, IImageBuffer imageBuffer) {
        super(null, url, fallbackTexture, imageBuffer);
    }

    public boolean isTextureUploaded() {
        return uploaded && this.getGlTextureId() > -1;
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        this.uploaded = true;
    }
}
