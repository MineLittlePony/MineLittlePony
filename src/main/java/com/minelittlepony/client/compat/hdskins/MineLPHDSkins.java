package com.minelittlepony.client.compat.hdskins;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.hdskins.HDSkinsServer;
import com.minelittlepony.hdskins.client.*;
import com.minelittlepony.hdskins.client.ducks.ClientPlayerInfo;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.profile.SkinType;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.item.Items;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.*;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends ClientSkinsProxy implements ClientModInitializer {

    static SkinType seaponySkinType;
    static SkinType nirikSkinType;

    static final Map<SkinType, Wearable> WEARABLE_TYPES = new HashMap<>();

    @Override
    public void onInitializeClient() {
        PonySettingsScreen.buttonFactory = this::renderOption;

        seaponySkinType = SkinType.register(DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, Items.COD_BUCKET.getDefaultStack());
        nirikSkinType = SkinType.register(DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, Items.LAVA_BUCKET.getDefaultStack());
        Wearable.REGISTRY.values().forEach(wearable -> {
            if (wearable != Wearable.NONE) {
                WEARABLE_TYPES.put(SkinType.register(wearable.getId(), Items.BUNDLE.getDefaultStack()), wearable);
            }
        });

        // Clear ponies when skins are cleared
        SkinCacheClearCallback.EVENT.register(() -> {
            MineLittlePony.getInstance().getManager().clearCache();
        });

        // Ponify the skins GUI.
        GuiSkins.setSkinsGui(GuiSkinsMineLP::new);

        HDSkins.getInstance().getSkinPrioritySorter().addSelector((skinType, playerSkins) -> {
            if (skinType == SkinType.SKIN) {
                Optional<Pony> hdPony = getPony(playerSkins.hd());
                Optional<Pony> vanillaPony = getPony(playerSkins.vanilla());

                if (hdPony.isPresent() && vanillaPony.isPresent()) {
                    PonyLevel level = PonyConfig.getInstance().ponyLevel.get();
                    boolean vanillaHuman = vanillaPony.get().metadata().race().isHuman();
                    boolean hdHuman = hdPony.get().metadata().race().isHuman();
                    if (vanillaHuman != hdHuman) {
                        return (level == PonyLevel.HUMANS ? vanillaHuman : hdHuman) ? playerSkins.vanilla() : playerSkins.hd();
                    }

                    if (vanillaPony.get().metadata().priority() > hdPony.get().metadata().priority()) {
                        return playerSkins.vanilla();
                    }
                }
            }

            return playerSkins.combined();
        });
    }

    static Optional<Pony> getPony(PlayerSkinLayers.Layer layer) {
        return layer
            .getSkin(SkinType.SKIN)
            .map(TextureAsset::texturePath)
            .map(Pony.getManager()::getPony);
    }

    private void renderOption(Screen screen, @Nullable Screen parent, int row, int RIGHT, ScrollContainer content) {
        content.addButton(new Button(RIGHT, row += 20, 150, 20))
            .onClick(button -> MinecraftClient.getInstance().setScreen(
                    parent instanceof GuiSkins ? parent : GuiSkins.create(screen, HDSkinsServer.getInstance().getServers())
            ))
            .getStyle()
                .setText("minelp.options.skins.hdskins.open");
    }

    @Override
    public Optional<Identifier> getSkin(Identifier skinTypeId, PlayerLikeEntity player) {
        return SkinType.REGISTRY.getOptionalValue(skinTypeId).flatMap(type -> getSkin(type, player));
    }

    public Set<Identifier> getAvailableSkins(Entity entity) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            return PlayerSkins.of(player)
                    .map(PlayerSkins::layers)
                    .map(PlayerSkinLayers::combined)
                    .map(PlayerSkinLayers.Layer::getProvidedSkinTypes)
                    .orElseGet(Set::of);
        }

        return Set.of();
    }

    private Optional<Identifier> getSkin(SkinType type, PlayerLikeEntity player) {
        return ClientPlayerInfo.of(player)
                .flatMap(skins -> skins.getSkins().layers().combined().getSkin(type))
                .map(skin -> skin.texturePath());
    }
}
