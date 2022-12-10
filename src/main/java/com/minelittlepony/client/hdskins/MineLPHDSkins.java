package com.minelittlepony.client.hdskins;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.hdskins.client.*;
import com.minelittlepony.hdskins.client.ducks.ClientPlayerInfo;
import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.client.resources.LocalPlayerSkins;
import com.minelittlepony.hdskins.mixin.client.MixinClientPlayer;
import com.minelittlepony.hdskins.profile.SkinType;

import com.mojang.authlib.GameProfile;

import java.util.*;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.entity.PlayerSeaponyRenderer;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends SkinsProxy implements ClientModInitializer {

    static SkinType seaponySkinType;

    static final Map<SkinType, Wearable> wearableTypes = new HashMap<>();

    @Override
    public void onInitializeClient() {
        SkinsProxy.instance = this;

        seaponySkinType = SkinType.register(PlayerSeaponyRenderer.SKIN_TYPE_ID, Items.COD_BUCKET.getDefaultStack());
        Wearable.VALUES.forEach(wearable -> {
            if (wearable != Wearable.NONE) {
                wearableTypes.put(SkinType.register(wearable.getId(), Items.BUNDLE.getDefaultStack()), wearable);
            }
        });

        ClientReadyCallback.EVENT.register(client -> {
            // Clear ponies when skins are cleared
            PonyManager ponyManager = (PonyManager) MineLittlePony.getInstance().getManager();
            SkinCacheClearCallback.EVENT.register(ponyManager::clearCache);

            // Ponify the skins GUI.
            GuiSkins.setSkinsGui(GuiSkinsMineLP::new);
        });
    }

    @Override
    public void renderOption(Screen screen, @Nullable Screen parent, int row, int RIGHT, ScrollContainer content) {
        content.addButton(new Button(RIGHT, row += 20, 150, 20))
            .onClick(button -> MinecraftClient.getInstance().setScreen(
                    parent instanceof GuiSkins ? parent : GuiSkins.create(screen, HDSkins.getInstance().getSkinServerList())
            ))
            .getStyle()
                .setText("minelp.options.skins.hdskins.open");
    }

    @Override
    public Optional<Identifier> getSkin(Identifier skinTypeId, AbstractClientPlayerEntity player) {
        return SkinType.REGISTRY.getOrEmpty(skinTypeId).flatMap(type -> getSkin(type, player));
    }

    public Set<Identifier> getAvailableSkins(Entity entity) {

        if (entity instanceof DummyPlayer dummy) {
            return SkinType.REGISTRY.stream()
                    .filter(type -> {
                        return dummy.getTextures().get(type).isReady()
                            || (dummy.getTextures().getPosture().getActiveSkinType() == type && dummy.getTextures() instanceof LocalPlayerSkins);
                    })
                    .map(SkinType::getId)
                    .collect(Collectors.toSet());
        }

        if (entity instanceof AbstractClientPlayerEntity player) {
            PlayerSkins skins = ((ClientPlayerInfo)((MixinClientPlayer)player).getBackingClientData()).getSkins();
            return SkinType.REGISTRY.stream()
                    .filter(type -> skins.getSkin(type) != null)
                    .map(SkinType::getId)
                    .collect(Collectors.toSet());
        }

        return Set.of();
    }

    private Optional<Identifier> getSkin(SkinType type, AbstractClientPlayerEntity player) {
        if (player instanceof DummyPlayer dummy) {
            return Optional.of(dummy.getTextures().get(type).getId());
        }

        return Optional.ofNullable(((ClientPlayerInfo)((MixinClientPlayer)player).getBackingClientData()).getSkins().getSkin(type));
    }

    @Override
    public Identifier getSkinTexture(GameProfile profile) {

        Identifier skin = HDSkins.getInstance().getProfileRepository().getTextures(profile).get(SkinType.SKIN);

        if (skin != null) {
            return skin;
        }

        return super.getSkinTexture(profile);
    }
}
