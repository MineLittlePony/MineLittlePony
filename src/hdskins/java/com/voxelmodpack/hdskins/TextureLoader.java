package com.voxelmodpack.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

public class TextureLoader {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static void loadTexture(final ResourceLocation textureLocation, final ITextureObject textureObj) {
        mc.addScheduledTask((Runnable) () -> mc.getTextureManager().loadTexture(textureLocation, textureObj));
    }
}
