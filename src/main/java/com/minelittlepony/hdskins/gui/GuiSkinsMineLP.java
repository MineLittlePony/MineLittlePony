package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.mojang.authlib.GameProfile;
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
    protected void onSetLocalSkin(BufferedImage skin) {
        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        ponyManager.getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).checkSkin(skin);
    }

    @Override
    protected void onSetRemoteSkin() {
        MineLittlePony.logger.debug("Invalidating old remove skin, checking updated remote skin");
        ponyManager.getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ponyManager.getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).invalidateSkinCheck();
        ponyManager.getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();

    }
}
