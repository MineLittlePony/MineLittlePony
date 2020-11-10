package com.minelittlepony.client;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class SkinsProxy {

    public static SkinsProxy instance = new SkinsProxy();

    @Nullable
    public Identifier getSkinTexture(GameProfile profile) {
        PlayerSkinProvider skins = MinecraftClient.getInstance().getSkinProvider();

        @Nullable
        MinecraftProfileTexture texture = skins.getTextures(profile).get(MinecraftProfileTexture.Type.SKIN);

        if (texture == null) {
            return null;
        }

        return skins.loadSkin(texture, MinecraftProfileTexture.Type.SKIN);
    }

    public Identifier getSeaponySkin(EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager, AbstractClientPlayerEntity player) {
        return manager.getPony(player).getTexture();
    }
}
