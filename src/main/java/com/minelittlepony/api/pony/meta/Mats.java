package com.minelittlepony.api.pony.meta;

import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public interface Mats {
    static TriggerPixel.Mat createMat(MinecraftProfileTexture texture) throws IOException {
        return createMat(URI.create(texture.getUrl()).toURL());
    }

    static TriggerPixel.Mat createMat(URL url) throws IOException {
        try {
            @Nullable
            BufferedImage image = ImageIO.read(url);
            if (image == null) {
                throw new IOException("Unable to read image from url " + url);
            }
            Raster raster = image.getData();
            return (x, y) -> {
                if (x < 0 || y < 0 || x > raster.getWidth() || y > raster.getHeight()) {
                    return 0;
                }
                int[] color = raster.getPixel(x, y, new int[] {0, 0, 0, 0});
                return ColorHelper.getArgb(color[3], color[0], color[1], color[2]);
            };
        } catch (IllegalArgumentException e) {
            throw new IOException("Could not create mat from image", e);
        }
    }
}
