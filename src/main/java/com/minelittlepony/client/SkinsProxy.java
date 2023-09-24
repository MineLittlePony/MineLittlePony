package com.minelittlepony.client;

import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.Tooltip;
import com.minelittlepony.common.client.gui.element.Button;
import com.mojang.authlib.GameProfile;

import java.util.Optional;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class SkinsProxy {

    public static SkinsProxy instance = new SkinsProxy();

    @Nullable
    public Identifier getSkinTexture(GameProfile profile) {
        PlayerSkinProvider skins = MinecraftClient.getInstance().getSkinProvider();
        return skins.getSkinTextures(profile).texture();
    }

    public void renderOption(Screen screen, @Nullable Screen parent, int row, int RIGHT, ScrollContainer content) {
        content.addButton(new Button(RIGHT, row += 20, 150, 20))
            .setEnabled(false)
            .getStyle()
                .setTooltip(Tooltip.of("minelp.options.skins.hdskins.disabled", 200))
                .setText("minelp.options.skins.hdskins.open");
    }

    public Optional<Identifier> getSkin(Identifier skinTypeId, AbstractClientPlayerEntity player) {
        return Optional.empty();
    }

    public Set<Identifier> getAvailableSkins(Entity entity) {
        return Set.of();
    }
}














