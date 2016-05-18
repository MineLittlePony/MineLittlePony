package com.brohoof.minelittlepony.hdskins.gui;

import java.awt.image.BufferedImage;

import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.PonyManager;
import com.brohoof.minelittlepony.PonyRace;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.TailLengths;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.gui.MetaHandler;

public class GuiSkinsMineLP extends GuiSkins {

    private PonyManager ponyManager;

    public GuiSkinsMineLP(PonyManager manager) {
        this.ponyManager = manager;
    }

    @Override
    protected void setupMetaOverrides(MetaHandler meta) {
        super.setupMetaOverrides(meta);
        meta.selection("race", PonyRace.class);
        meta.selection("tail", TailLengths.class);
        meta.selection("gender", PonyGender.class);
        meta.selection("size", PonySize.class);
        meta.color("magic");
    }

    @Override
    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPonyModel(profile);
    }

    @Override
    protected void onSetLocalSkin(BufferedImage skin) {
        MineLPLogger.debug("Invalidating old local skin, checking updated local skin");
        ponyManager.getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).checkSkin(skin);
    }

    @Override
    protected void onSetRemoteSkin() {
        MineLPLogger.debug("Invalidating old remove skin, checking updated remote skin");
        ponyManager.getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ponyManager.getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).invalidateSkinCheck();
        ponyManager.getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();

    }
}
