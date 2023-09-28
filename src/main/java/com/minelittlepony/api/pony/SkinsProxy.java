package com.minelittlepony.api.pony;

import com.mojang.authlib.GameProfile;

import java.util.Optional;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * Proxy handler for getting player skin data from HDSkins
 */
public class SkinsProxy {
    public static SkinsProxy instance = new SkinsProxy();

    public Identifier getSkinTexture(GameProfile profile) {
        PlayerSkinProvider skins = MinecraftClient.getInstance().getSkinProvider();
        return skins.getSkinTextures(profile).texture();
    }

    public Optional<Identifier> getSkin(Identifier skinTypeId, AbstractClientPlayerEntity player) {
        return Optional.empty();
    }

    public Set<Identifier> getAvailableSkins(Entity entity) {
        return Set.of();
    }
}
