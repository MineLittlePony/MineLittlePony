package com.minelittlepony.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.pony.SkinsProxy;
import com.mojang.authlib.GameProfile;

public class ClientSkinsProxy extends SkinsProxy {
    @Nullable
    public Identifier getSkinTexture(@Nullable GameProfile profile) {
        if (profile == null) {
            return null;
        }
        return Minecraft.getInstance().playerSkinRenderCache().getOrDefault(ResolvableProfile.createResolved(profile)).playerSkin().body().texturePath();
    }
}
