package com.minelittlepony.client.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.List;

public class TextureFlattener {

    public static void flatten(List<Identifier> textures, Identifier output) {
        Preconditions.checkArgument(textures.size() > 0, "Must have at least one image to flatten");
        MinecraftClient.getInstance().getTextureManager().registerTexture(output, new ResourceTexture(output) {
            @Override
            protected TextureData loadTextureData(ResourceManager resourceManager) {
                try {
                    NativeImage image = NativeImage.read(resourceManager.getResourceOrThrow(textures.get(0)).getInputStream());

                    for (int i = 1; i < textures.size(); i++) {
                        try (NativeImage data = NativeImage.read(resourceManager.getResourceOrThrow(textures.get(i)).getInputStream())) {
                            copyOver(data, image);
                        }
                    }

                    return new TextureData(null, image);
                } catch (IOException e) {
                    return new TextureData(e);
                }
            }
        });
    }

    public static void copyOver(NativeImage from, NativeImage to) {
        copyOver(from, to, 0, 0,
                Math.min(from.getWidth(), to.getWidth()),
                Math.min(from.getHeight(), to.getHeight())
        );
    }

    public static void copyOver(NativeImage from, NativeImage to, int x, int y, int w, int h) {
        for (int xx = x; xx < w; xx++) {
            for (int yy = y; yy < h; yy++) {
                copy(from, to, xx, yy);
            }
        }
    }

    public static void copy(NativeImage from, NativeImage to, int x, int y) {
        int color = from.getColor(x, y);
        if (ColorHelper.Argb.getAlpha(color) > 0) {
            to.setColor(x, y, color);
        }
    }
}
