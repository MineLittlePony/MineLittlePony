package com.voxelmodpack.hdskins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

public class LocalTexture {

    private final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

    private DynamicTexture local;
    private PreviewTexture remote;

    private ResourceLocation remoteResource;
    private ResourceLocation localResource;

    private final IBlankSkinSupplier blank;

    private final Type type;

    public LocalTexture(GameProfile profile, Type type, IBlankSkinSupplier blank) {
        this.blank = blank;
        this.type = type;

        String file =  type.name().toLowerCase() + "s/preview_${profile.getName()}.png";

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

        remote = ptm.getPreviewTexture(remoteResource, type, blank.getBlankSkin(type), callback);
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
