package com.voxelmodpack.hdskins.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.voxelmodpack.hdskins.DynamicTextureImage;
import com.voxelmodpack.hdskins.ImageBufferDownloadHD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

public class SkinThread implements Runnable {

    private ResourceLocation original;
    private ResourceLocation updated;
    private BufferedImage image;

    public SkinThread(ResourceLocation loc) {
        this.original = loc;
        new Thread(this).start();
    }

    @Override
    public void run() {
        image = new ImageBufferDownloadHD().parseUserSkin(getImage(original));
    }

    @Nullable
    private static BufferedImage getImage(ResourceLocation res) {
        try {
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream();
            try {
                return TextureUtil.readBufferedImage(in);
            } finally {
                IOUtils.closeQuietly(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResourceLocation getResource() {
        return this.updated;
    }

    public void deleteTexture() {
        Minecraft.getMinecraft().getTextureManager().deleteTexture(updated);
    }

    public boolean isReady() {
        return image != null;
    }

    public void uploadSkin() {

        ResourceLocation conv = new ResourceLocation("hdskins-converted", original.getResourcePath());
        Minecraft.getMinecraft().getTextureManager().loadTexture(conv, new DynamicTextureImage(image));
        updated = conv;

        image = null;
    }
}
