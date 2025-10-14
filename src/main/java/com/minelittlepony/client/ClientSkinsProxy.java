package com.minelittlepony.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.SkinsProxy;
import com.mojang.authlib.GameProfile;

public class ClientSkinsProxy extends SkinsProxy {
    @Nullable
    public Identifier getSkinTexture(@Nullable GameProfile profile) {
        if (profile == null) {
            return null;
        }
        return MinecraftClient.getInstance().getPlayerSkinCache().get(ProfileComponent.ofStatic(profile)).getTextures().body().texturePath();
    }
}
