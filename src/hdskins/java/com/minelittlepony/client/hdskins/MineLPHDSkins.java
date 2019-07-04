package com.minelittlepony.client.hdskins;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.ClientReadyCallback;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.hdskins.gui.DummyPony;
import com.minelittlepony.client.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.client.hdskins.gui.RenderDummyPony;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

import com.minelittlepony.client.pony.PonyManager;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.net.LegacySkinServer;
import com.minelittlepony.hdskins.net.SkinServer;
import com.minelittlepony.hdskins.net.ValhallaSkinServer;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins {
    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";

    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    public MineLPHDSkins() {
        SkinsProxy.instance = new HDSkinsProxy();

        SkinServer legacy = new LegacySkinServer(MINELP_LEGACY_SERVER, MINELP_LEGACY_GATEWAY);
        SkinServer valhalla = new ValhallaSkinServer(MINELP_VALHALLA_SERVER);
        // Register pony servers
        HDSkins.getInstance().addSkinServer(legacy);
        HDSkins.getInstance().addSkinServer(valhalla);

        SkinServer.defaultServers.add(legacy);
        // And make valhalla the default
        SkinServer.defaultServers.add(0, valhalla);

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
        manager.setSkinsGui(GuiSkinsMineLP::new);
    }
}
