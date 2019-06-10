package com.minelittlepony.client.gui.hdskins;

import net.minecraft.client.MinecraftClient;

import com.minelittlepony.common.client.IModUtilities;
import com.minelittlepony.client.MineLPClient;
import com.minelittlepony.client.PonySkinModifier;
import com.minelittlepony.client.PonySkinParser;

import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.net.LegacySkinServer;
import com.minelittlepony.hdskins.net.SkinServer;
import com.minelittlepony.hdskins.net.ValhallaSkinServer;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends MineLPClient {
    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";

    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    public MineLPHDSkins(IModUtilities utils) {
        super(utils);

        SkinServer legacy = new LegacySkinServer(MINELP_LEGACY_SERVER, MINELP_LEGACY_GATEWAY);
        SkinServer valhalla = new ValhallaSkinServer(MINELP_VALHALLA_SERVER);
        // Register pony servers
        HDSkins.getInstance().addSkinServer(legacy);
        HDSkins.getInstance().addSkinServer(valhalla);

        SkinServer.defaultServers.add(legacy);
        // And make valhalla the default
        SkinServer.defaultServers.add(0, valhalla);
    }

    /**
     * Called when the game is ready.
     */
    @Override
    public void postInit(MinecraftClient minecraft) {
        super.postInit(minecraft);

        HDSkins manager = HDSkins.getInstance();

        // Convert legacy pony skins
        manager.addSkinModifier(new PonySkinModifier());
        // Parse trigger pixel data
        manager.addSkinParser(new PonySkinParser());
        // Clear ponies when skins are cleared
        manager.addClearListener(getManager());
        // Ponify the skins GUI.
        manager.setSkinsGui(GuiSkinsMineLP::new);
    }
}
