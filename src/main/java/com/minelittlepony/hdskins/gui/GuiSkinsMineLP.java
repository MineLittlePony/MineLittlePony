package com.minelittlepony.hdskins.gui;

import java.awt.image.BufferedImage;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyGender;
import com.minelittlepony.PonyManager;
import com.minelittlepony.PonyRace;
import com.minelittlepony.PonySize;
import com.minelittlepony.TailLengths;
import com.minelittlepony.util.MineLPLogger;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.gui.GuiMetaHandler;

public class GuiSkinsMineLP extends GuiSkins {

    private PonyManager ponyManager;

    public GuiSkinsMineLP(PonyManager manager) {
        this.ponyManager = manager;
    }

    @Override
    protected void setupMetaOverrides(GuiMetaHandler meta) {
        super.setupMetaOverrides(meta);
        meta.selection(MineLittlePony.MLP_RACE, PonyRace.class);
        meta.selection(MineLittlePony.MLP_TAIL, TailLengths.class);
        meta.selection(MineLittlePony.MLP_GENDER, PonyGender.class);
        meta.selection(MineLittlePony.MLP_SIZE, PonySize.class);
        meta.color(MineLittlePony.MLP_MAGIC);
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
