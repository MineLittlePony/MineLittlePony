package com.minelittlepony.client.compat.hdskins;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.PonyData;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.hdskins.client.*;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.client.gui.player.DummyPlayer;
import com.minelittlepony.hdskins.client.gui.player.skins.PlayerSkins.PlayerSkin;
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

import com.minelittlepony.client.*;
import com.minelittlepony.client.render.entity.AquaticPlayerPonyRenderer;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends SkinsProxy implements ClientModInitializer {

    static SkinType seaponySkinType;

    static final Map<SkinType, Wearable> wearableTypes = new HashMap<>();

    @Override
    public void onInitializeClient() {
        SkinsProxy.instance = this;

        seaponySkinType = SkinType.register(AquaticPlayerPonyRenderer.SKIN_TYPE_ID, Items.COD_BUCKET.getDefaultStack());
        Wearable.REGISTRY.values().forEach(wearable -> {
            if (wearable != Wearable.NONE) {
                wearableTypes.put(SkinType.register(wearable.getId(), Items.BUNDLE.getDefaultStack()), wearable);
            }
        });

        ClientReadyCallback.EVENT.register(client -> {
            // Clear ponies when skins are cleared
            SkinCacheClearCallback.EVENT.register(MineLittlePony.getInstance().getManager()::clearCache);

            // Ponify the skins GUI.
            GuiSkins.setSkinsGui(GuiSkinsMineLP::new);
        });

        HDSkins.getInstance().getSkinPrioritySorter().addSelector((skinType, playerSkins) -> {
            if (skinType == SkinType.SKIN && PonyConfig.getInstance().mixedHumanSkins.get()) {
                PonyLevel level = PonyConfig.getInstance().ponyLevel.get();

                if (level == PonyLevel.HUMANS && isPony(playerSkins.hd()) && !isPony(playerSkins.vanilla())) {
                    return playerSkins.vanilla();
                }
            }
            return playerSkins.combined();
        });
    }

    static boolean isPony(PlayerSkins.Layer layer) {
        return layer
            .getSkin(SkinType.SKIN)
            .map(Pony.getManager()::getPony)
            .filter(pony -> !pony.metadata().race().isHuman())
            .isPresent();
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
            return PlayerSkins.of(player)
                    .map(PlayerSkins::combined)
                    .map(PlayerSkins.Layer::getProvidedSkinTypes)
                    .orElseGet(Set::of);
        }

        return Set.of();
    }

    private Optional<Identifier> getSkin(SkinType type, AbstractClientPlayerEntity player) {
        if (player instanceof DummyPlayer dummy) {
            PlayerSkin skin = dummy.getTextures().get(type);

            if (skin.isReady() || getAvailableSkins(player).contains(type.getId())) {
                return Optional.of(skin.getId());
            }

            PlayerSkin main = dummy.getTextures().get(SkinType.SKIN);
            Wearable wearable = Wearable.REGISTRY.getOrDefault(type.getId(), Wearable.NONE);
            PonyData metadata = Pony.getManager().getPony(main.getId()).metadata();
            if (wearable != Wearable.NONE && metadata.gear().matches(wearable)) {

                if (wearable.isSaddlebags() && metadata.race().supportsLegacySaddlebags()) {
                    return Optional.of(main.getId());
                }

                return Optional.of(wearable.getDefaultTexture());
            }
        }

        return Optional.of(player).flatMap(PlayerSkins::of).map(PlayerSkins::combined).flatMap(skins -> skins.getSkin(type));
    }

    @Override
    public Identifier getSkinTexture(GameProfile profile) {
        return HDSkins.getInstance().getProfileRepository()
                .getNow(profile)
                .getSkin(SkinType.SKIN)
                .orElseGet(() -> super.getSkinTexture(profile));
    }
}
