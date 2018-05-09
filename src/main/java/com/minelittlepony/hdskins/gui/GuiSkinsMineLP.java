package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
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
    protected void onSetLocalSkin(Type type) {
        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        if (type == Type.SKIN) {
            ponyManager.removePony(localPlayer.getSkinTexture());
        }
    }

    @Override
    protected void onSetRemoteSkin(Type type, ResourceLocation resource, MinecraftProfileTexture profileTexture) {
        MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
        if (type == Type.SKIN) {
            ponyManager.removePony(resource);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ponyManager.removePony(localPlayer.getSkinTexture());
        ponyManager.removePony(remotePlayer.getSkinTexture());

    }
}
