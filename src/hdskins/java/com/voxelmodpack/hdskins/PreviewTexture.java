package com.voxelmodpack.hdskins;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class PreviewTexture extends ThreadDownloadImageData {

    private boolean uploaded;

    private String model;

    public PreviewTexture(MinecraftProfileTexture texture, ResourceLocation fallbackTexture, @Nullable IImageBuffer imageBuffer) {
        super(null,  texture.getUrl(), fallbackTexture, imageBuffer);

        model = texture.getMetadata("model");
        if (model == null) {
            model = "default";
        }
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

    public String getModel() {
        return model;
    }
}
