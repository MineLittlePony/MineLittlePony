package com.voxelmodpack.hdskins.resources;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.resources.texture.DynamicTextureImage;
import com.voxelmodpack.hdskins.resources.texture.ImageBufferDownloadHD;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LocalTexture {

    private final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

    private DynamicTexture local;
    private PreviewTexture remote;

    private ResourceLocation remoteResource;
    private ResourceLocation localResource;

    private final IBlankSkinSupplier blank;

    private final Type type;

    private boolean remoteLoaded = false;

    public LocalTexture(GameProfile profile, Type type, IBlankSkinSupplier blank) {
        this.blank = blank;
        this.type = type;

        String file = String.format("%s/preview_%s.png", type.name().toLowerCase(), profile.getName());

        remoteResource = new ResourceLocation(file);
        textureManager.deleteTexture(remoteResource);


        reset();
    }

    public ResourceLocation getTexture() {
        if (hasRemote()) {
            return remoteResource;
        }

        return localResource;
    }

    public void reset() {
        localResource = blank.getBlankSkin(type);
    }

    public boolean hasRemote() {
        return remote != null;
    }

    public boolean hasLocal() {
        return local != null;
    }

    public boolean hasRemoteTexture() {
        return uploadComplete() && remoteLoaded;
    }

    public boolean usingLocal() {
        return !hasRemote() && hasLocal();
    }

    public boolean uploadComplete() {
        return hasRemote() && remote.isTextureUploaded();
    }

    public PreviewTexture getRemote() {
        return remote;
    }

    public void setRemote(PreviewTextureManager ptm, SkinAvailableCallback callback) {
        clearRemote();

        remote = ptm.getPreviewTexture(remoteResource, type, blank.getBlankSkin(type), (type, location, profileTexture) -> {
            if (callback != null) {
                callback.skinAvailable(type, location, profileTexture);
            }
            remoteLoaded = true;
        });
    }

    public void setLocal(File file) {
        if (!file.exists()) {
            return;
        }

        clearLocal();

        try {
            BufferedImage image = ImageIO.read(file);
            BufferedImage bufferedImage = new ImageBufferDownloadHD().parseUserSkin(image);

            local = new DynamicTextureImage(bufferedImage);
            localResource = textureManager.getDynamicTextureLocation("localSkinPreview", local);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearRemote() {
        remoteLoaded = false;
        if (hasRemote()) {
            remote = null;
            textureManager.deleteTexture(remoteResource);
        }
    }

    public void clearLocal() {
        if (hasLocal()) {
            local = null;
            textureManager.deleteTexture(localResource);
            localResource = blank.getBlankSkin(type);
        }
    }

    public interface IBlankSkinSupplier {

        ResourceLocation getBlankSkin(Type type);
    }
}
