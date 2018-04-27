package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import net.minecraft.util.ResourceLocation;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
public class GuiSkinsMineLP extends GuiSkins {

    private PonyManager ponyManager;

    public GuiSkinsMineLP(PonyManager manager) {
        ponyManager = manager;
    }

    @Override
    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPonyModel(profile);
    }

    @Override
    protected void onSetLocalSkin(MinecraftProfileTexture.Type type) {
        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        if (type == MinecraftProfileTexture.Type.SKIN) {
            ponyManager.removePony(localPlayer.getSkinTexture());
        }
    }

    @Override
    protected void onSetRemoteSkin(MinecraftProfileTexture.Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
        MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
        if (type == MinecraftProfileTexture.Type.SKIN) {
            ponyManager.removePony(location);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ponyManager.removePony(localPlayer.getSkinTexture());
        ponyManager.removePony(remotePlayer.getSkinTexture());

    }
}
