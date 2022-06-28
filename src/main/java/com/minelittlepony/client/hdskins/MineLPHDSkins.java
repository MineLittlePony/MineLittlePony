package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.hdskins.client.SkinCacheClearCallback;
import com.minelittlepony.hdskins.client.ducks.ClientPlayerInfo;
import com.minelittlepony.hdskins.client.dummy.DummyPlayer;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.client.resources.LocalTexture;
import com.minelittlepony.hdskins.mixin.client.MixinClientPlayer;
import com.minelittlepony.hdskins.profile.SkinType;

import com.mojang.authlib.GameProfile;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.hdskins.client.HDSkins;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends SkinsProxy implements ClientModInitializer {

    static SkinType seaponySkinType;

    @Override
    public void onInitializeClient() {
        SkinsProxy.instance = this;

        seaponySkinType = SkinType.register(new Identifier("minelp", "seapony"), Items.COD_BUCKET.getDefaultStack());

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
    public Identifier getSeaponySkin(EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager, AbstractClientPlayerEntity player) {
        if (player instanceof DummyPlayer) {
            LocalTexture tex = ((DummyPlayer)player).getTextures().get(seaponySkinType);
            Identifier id = tex.getId();
            return id == null ? tex.getDefault() : id;
        } else {
            ClientPlayerInfo info = (ClientPlayerInfo)((MixinClientPlayer)player).getBackingClientData();
            Identifier tex = info.getSkins().getSkin(seaponySkinType);
            if (tex != null) {
                return tex;
            }
        }

        return super.getSeaponySkin(manager, player);
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
