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

    private volatile DynamicTexture local;
    private volatile PreviewTexture remote;

    private ResourceLocation remoteResource;
    private ResourceLocation localResource;

    private final IBlankSkinSupplier blank;

    private final Type type;

    public LocalTexture(GameProfile profile, Type type, IBlankSkinSupplier blank) {
        this.blank = blank;
        this.type = type;

        String file = String.format("%ss/preview_%s.png", type.name().toLowerCase(), profile.getName());

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

        ptm.getPreviewTexture(remoteResource, type, blank.getBlankSkin(type), callback).thenAccept(texture -> {
            remote = texture;
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
