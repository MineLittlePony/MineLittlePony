package com.brohoof.minelittlepony.hdskins.gui;

import java.awt.image.BufferedImage;

import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.hdskins.gui.EntityPonyModel;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;

public class GuiSkinsMineLP extends GuiSkins {

    @Override
    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPonyModel(profile);
    }

    @Override
    protected void onSetLocalSkin(BufferedImage skin) {
        MineLPLogger.debug("Invalidating old local skin, checking updated local skin");
        PonyManager.getInstance().getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).checkSkin(skin);
    }

    @Override
    protected void onSetRemoteSkin() {
        MineLPLogger.debug("Invalidating old remove skin, checking updated remote skin");
        PonyManager.getInstance().getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        PonyManager.getInstance().getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).invalidateSkinCheck();
        PonyManager.getInstance().getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();

    }
}
