package com.voxelmodpack.hdskins;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PreviewTexture extends ThreadDownloadImageData {

    private boolean uploaded;

    private String model;

    public PreviewTexture(@Nullable String model, String url, ResourceLocation fallbackTexture, @Nullable IImageBuffer imageBuffer) {
        super(null, url, fallbackTexture, imageBuffer);

        this.model = model == null ? "default" : model;
    }

    public boolean isTextureUploaded() {
        return uploaded && getGlTextureId() > -1;
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        uploaded = true;
    }

    public boolean hasModel() {
        return model != null;
    }

    public boolean usesThinArms() {
        return "thin".equals(model);
    }
}
