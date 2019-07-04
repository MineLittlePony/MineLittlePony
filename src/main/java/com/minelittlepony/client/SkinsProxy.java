package com.minelittlepony.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class SkinsProxy {

    public static SkinsProxy instance = new SkinsProxy();

    @Nullable
    public Identifier getSkinTexture(GameProfile profile) {
        PlayerSkinProvider skins = MinecraftClient.getInstance().getSkinProvider();

        MinecraftProfileTexture texture = skins.getTextures(profile).get(MinecraftProfileTexture.Type.SKIN);
        return skins.loadSkin(texture, MinecraftProfileTexture.Type.SKIN);
    }
}
