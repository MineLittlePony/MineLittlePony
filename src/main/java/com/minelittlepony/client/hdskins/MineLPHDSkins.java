package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.hdskins.gui.DummyPony;
import com.minelittlepony.client.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.client.hdskins.gui.RenderDummyPony;
import com.minelittlepony.common.event.ClientReadyCallback;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.hdskins.HDSkins;

/**
 * All the interactions with HD Skins.
 */
class MineLPHDSkins {

    MineLPHDSkins() {
        SkinsProxy.instance = new HDSkinsProxy();

        ClientReadyCallback.EVENT.register(this::postInit);

        // Preview on the select skin gui
        EntityRendererRegistry.INSTANCE.register(DummyPony.class, RenderDummyPony::new);
    }

    private void postInit(MinecraftClient minecraft) {
        HDSkins manager = HDSkins.getInstance();

        // Clear ponies when skins are cleared
        PonyManager ponyManager = (PonyManager) MineLittlePony.getInstance().getManager();
        manager.addClearListener(ponyManager::onSkinCacheCleared);

        // Ponify the skins GUI.
        manager.getSkinServerList().setSkinsGui(GuiSkinsMineLP::new);
    }
}
