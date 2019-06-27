package com.minelittlepony.client.hdskins;

import com.minelittlepony.client.hdskins.gui.DummyPony;
import com.minelittlepony.client.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.client.hdskins.gui.RenderDummyPony;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLPClient;

import com.minelittlepony.client.settings.ClientPonyConfig;
import com.minelittlepony.client.LegacySkinConverter;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.hdskins.net.LegacySkinServer;
import com.minelittlepony.hdskins.net.SkinServer;
import com.minelittlepony.hdskins.net.ValhallaSkinServer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import javax.annotation.Nullable;

import java.util.Map;

/**
 * All the interactions with HD Skins.
 */
public class MineLPHDSkins extends MineLPClient {
    private static final String MINELP_VALHALLA_SERVER = "http://skins.minelittlepony-mod.com";

    private static final String MINELP_LEGACY_SERVER = "http://minelpskins.voxelmodpack.com";
    private static final String MINELP_LEGACY_GATEWAY = "http://minelpskinmanager.voxelmodpack.com";

    public MineLPHDSkins() {
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

        // Preview on the select skin gui
        EntityRendererRegistry.INSTANCE.register(DummyPony.class, RenderDummyPony::new);

        HDSkins manager = HDSkins.getInstance();

        // Convert legacy pony skins
        manager.addSkinModifier(new LegacySkinConverter());
        // Parse trigger pixel data
        manager.addSkinParser(new PonySkinParser());
        // Clear ponies when skins are cleared
        manager.addClearListener(getManager()::onSkinCacheCleared);
        // Ponify the skins GUI.
        manager.setSkinsGui(GuiSkinsMineLP::new);
    }

    @Override
    protected ClientPonyConfig createConfig() {
        return new ClientPonyConfigHDSkins();
    }

    @Override
    public Map<MinecraftProfileTexture.Type, Identifier> getProfileTextures(@Nullable GameProfile profile) {
        return HDSkins.getInstance().getTextures(profile);
    }
}
