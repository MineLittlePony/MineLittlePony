package com.voxelmodpack.hdskins;

import com.minelittlepony.avatar.texture.TextureProfile;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PreviewTexture extends ThreadDownloadImageData {

    private boolean uploaded;

    private String model;

    public PreviewTexture(TextureProfile profile, ResourceLocation fallbackTexture, @Nullable IImageBuffer imageBuffer) {
        super(null, profile.getUrl().toString(), fallbackTexture, imageBuffer);

        this.model = profile.getMetadata("model").orElse("default");
    }

    public boolean isTextureUploaded() {
        return uploaded && this.getGlTextureId() > -1;
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
        return "thin".equals(model);
    }
}
