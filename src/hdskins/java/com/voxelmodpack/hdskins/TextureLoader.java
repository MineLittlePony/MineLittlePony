package com.voxelmodpack.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

public class TextureLoader {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void loadTexture(ResourceLocation textureLocation, ITextureObject textureObj) {
        mc.addScheduledTask(() -> {
            mc.getTextureManager().loadTexture(textureLocation, textureObj);
        });
    }
}
