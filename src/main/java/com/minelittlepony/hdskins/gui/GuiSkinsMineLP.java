package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;

import java.awt.image.BufferedImage;

public class GuiSkinsMineLP extends GuiSkins {

    private PonyManager ponyManager;

    public GuiSkinsMineLP(PonyManager manager) {
        this.ponyManager = manager;
    }

    @Override
    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPonyModel(profile);
    }

    @Override
    protected void onSetLocalSkin(BufferedImage skin, MinecraftProfileTexture.Type type) {
        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        ponyManager.removePony(this.localPlayer.getSkinTexture());
    }

    @Override
    protected void onSetRemoteSkin(MinecraftProfileTexture.Type type) {
        MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
        ponyManager.removePony(this.remotePlayer.getSkinTexture());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ponyManager.removePony(this.localPlayer.getSkinTexture());
        ponyManager.removePony(this.remotePlayer.getSkinTexture());

    }
}
