package com.voxelmodpack.hdskins;

import java.io.File;

import com.voxelmodpack.common.runtime.PrivateFields;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public class PreviewTexture extends ThreadDownloadImageData {
    public PreviewTexture(String url, ResourceLocation fallbackTexture, IImageBuffer imageBuffer) {
        super((File) null, url, fallbackTexture, imageBuffer);
    }

    public boolean isTextureUploaded() {
        return PrivateFields.downloadedImage.get(this) != null && this.getGlTextureId() > -1;
    }
}
