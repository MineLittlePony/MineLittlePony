package com.minelittlepony.hdskins.resources;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import com.minelittlepony.hdskins.VanillaModels;

import javax.annotation.Nullable;

public class PreviewTexture extends ThreadDownloadImageData {

    private boolean uploaded;

    private String model;

    private String fileUrl;

    public PreviewTexture(@Nullable String model, String url, ResourceLocation fallbackTexture, @Nullable IImageBuffer imageBuffer) {
        super(null, url, fallbackTexture, imageBuffer);

        this.model = VanillaModels.nonNull(model);
        this.fileUrl = url;
    }

    public boolean isTextureUploaded() {
        return uploaded && getGlTextureId() > -1;
    }

    public String getUrl() {
        return fileUrl;
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
        return VanillaModels.isSlim(model);
    }
}
