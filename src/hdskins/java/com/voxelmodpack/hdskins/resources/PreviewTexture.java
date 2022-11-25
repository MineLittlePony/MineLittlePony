package com.voxelmodpack.hdskins.resources;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import com.voxelmodpack.hdskins.VanillaModels;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.Nullable;

public class PreviewTexture extends ThreadDownloadImageData {

    private boolean uploaded;

    private String model;

    private String fileUrl;

    public PreviewTexture(@Nullable String model, String url, ResourceLocation fallbackTexture, @Nullable IImageBuffer imageBuffer) {
        super(null, url, fallbackTexture, imageBuffer);

        this.model = VanillaModels.of(model);
        this.fileUrl = url;
    }

    public boolean isTextureUploaded() {
        return uploaded && this.getGlTextureId() > -1;
    }

    public InputStream openStream() throws IOException {
        return new URL(fileUrl).openStream();
    }

    public String getUrl() {
        return fileUrl;
    }

    @Override
    public void deleteGlTexture() {
        super.deleteGlTexture();
        this.uploaded = true;
    }

    public boolean hasModel() {
        return model != null;
    }

    public boolean usesThinArms() {
        return VanillaModels.isSlim(model);
    }
}
