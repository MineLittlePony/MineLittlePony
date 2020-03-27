package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.common.event.ClientReadyCallback;
import com.minelittlepony.hdskins.client.SkinCacheClearCallback;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.profile.SkinType;
import com.minelittlepony.mson.api.Mson;
import com.mojang.authlib.GameProfile;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.hdskins.client.HDSkins;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends SkinsProxy implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SkinsProxy.instance = this;

        ClientReadyCallback.EVENT.register(client -> {
            // Clear ponies when skins are cleared
            PonyManager ponyManager = (PonyManager) MineLittlePony.getInstance().getManager();
            SkinCacheClearCallback.EVENT.register(ponyManager::onSkinCacheCleared);

            // Ponify the skins GUI.
            GuiSkins.setSkinsGui(GuiSkinsMineLP::new);
        });

        // Preview on the select skin gui
        Mson.getInstance().getEntityRendererRegistry().registerEntityRenderer(DummyPony.TYPE, DummyPonyRenderer::new);
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
