package com.minelittlepony.client.hdskins;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.hdskins.client.*;
import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.dummy.PlayerSkins.PlayerSkin;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.profile.SkinType;

import com.mojang.authlib.GameProfile;

import java.util.*;

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
            return dummy.getTextures().getProvidedSkinTypes();
        }

        if (entity instanceof AbstractClientPlayerEntity player) {
            PlayerSkins skins = PlayerSkins.of(player);
            if (skins != null) {
                return skins.getProvidedSkinTypes();
            }
        }

        return Set.of();
    }

    private Optional<Identifier> getSkin(SkinType type, AbstractClientPlayerEntity player) {
        if (player instanceof DummyPlayer dummy) {
            PlayerSkin skin = dummy.getTextures().get(type);

            if (skin.isReady()) {
                return Optional.of(skin.getId());
            }

            PlayerSkin main = dummy.getTextures().get(SkinType.SKIN);
            if (IPony.getManager().getPony(main.getId()).metadata().isWearing(Wearable.REGISTRY.getOrDefault(type.getId(), Wearable.NONE))) {
                return Optional.of(main.getId());
            }
        }

        return Optional.of(player).map(PlayerSkins::of).map(skins -> skins.getSkin(type));
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
