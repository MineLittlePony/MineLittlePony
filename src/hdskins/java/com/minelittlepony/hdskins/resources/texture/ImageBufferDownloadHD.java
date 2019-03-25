package com.minelittlepony.hdskins.resources.texture;

import net.minecraft.client.renderer.texture.NativeImage;

import com.minelittlepony.hdskins.HDSkinManager;
import com.minelittlepony.hdskins.ISkinModifier;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import javax.annotation.Nullable;
import java.awt.Graphics;

public class ImageBufferDownloadHD implements SimpleDrawer, ISkinAvailableCallback, ISkinModifier.IDrawer {

    private int scale;
    private Graphics graphics;
    private NativeImage image;

    private ISkinAvailableCallback callback = null;

    private Type skinType = Type.SKIN;

    public ImageBufferDownloadHD() {

    }

    public ImageBufferDownloadHD(Type type, ISkinAvailableCallback callback) {
        this.callback = callback;
        this.skinType = type;
    }

    @Override
    @Nullable
    @SuppressWarnings({"SuspiciousNameCombination", "NullableProblems"})
    public NativeImage parseUserSkin(@Nullable NativeImage downloadedImage) {
        // TODO: Do we want to convert other skin types?
        if (downloadedImage == null || skinType != Type.SKIN) {
            return downloadedImage;
        }

        int imageWidth = downloadedImage.getWidth();
        int imageHeight = downloadedImage.getHeight();
        if (imageHeight == imageWidth) {
            return downloadedImage;
        }
        scale = imageWidth / 64;
        image = new NativeImage(imageWidth, imageWidth, true);
        image.copyImageData(downloadedImage);

        // copy layers
        // leg
        draw(scale, 24, 48, 20, 52,  4, 16,  8, 20); // top
        draw(scale, 28, 48, 24, 52,  8, 16, 12, 20); // bottom
        draw(scale, 20, 52, 16, 64,  8, 20, 12, 32); // inside
        draw(scale, 24, 52, 20, 64,  4, 20,  8, 32); // front
        draw(scale, 28, 52, 24, 64,  0, 20,  4, 32); // outside
        draw(scale, 32, 52, 28, 64, 12, 20, 16, 32); // back
        // arm
        draw(scale, 40, 48, 36, 52, 44, 16, 48, 20); // top
        draw(scale, 44, 48, 40, 52, 48, 16, 52, 20); // bottom
        draw(scale, 36, 52, 32, 64, 48, 20, 52, 32);
        draw(scale, 40, 52, 36, 64, 44, 20, 48, 32);
        draw(scale, 44, 52, 40, 64, 40, 20, 44, 32);
        draw(scale, 48, 52, 44, 64, 52, 20, 56, 32);

        // mod things
        HDSkinManager.INSTANCE.convertSkin(this);

        graphics.dispose();

        if (callback != null) {
            return callback.parseUserSkin(image);
        }

        return image;
    }

    @Override
    public void skinAvailable() {
        if (callback != null) {
            callback.skinAvailable();
        }
    }

    @Override
    public NativeImage getImage() {
        return image;
    }

}
