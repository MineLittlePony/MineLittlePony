package com.minelittlepony.client.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.NativeImage;

import java.io.IOException;
import java.util.List;

public class TextureFlattener {

    public static void flatten(List<Identifier> textures, Identifier output) {
        Preconditions.checkArgument(textures.size() > 0, "Must have at least one image to flatten");
        Minecraft.getInstance().getTextureManager().registerAndLoad(output, new SimpleTexture(output) {
            @Override
            public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
                NativeImage image = NativeImage.read(resourceManager.getResourceOrThrow(textures.get(0)).open());

                for (int i = 1; i < textures.size(); i++) {
                    try (NativeImage data = NativeImage.read(resourceManager.getResourceOrThrow(textures.get(i)).open())) {
                        copyOver(data, image);
                    }
                }

                return new TextureContents(image, null);
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
        int color = from.getPixel(x, y);
        if (ARGB.alpha(color) > 0) {
            to.setPixel(x, y, color);
        }
    }
}
