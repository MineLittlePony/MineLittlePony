package com.minelittlepony.minelp.hdskins.gui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.minelittlepony.minelp.PonyManager;
import com.minelittlepony.minelp.util.MineLPLogger;
import com.mojang.authlib.GameProfile;
import com.voxelmodpack.hdskins.gui.GuiSkins;

import net.minecraft.client.Minecraft;

class dGuiSkinsMineLP extends GuiSkins {

    public dGuiSkinsMineLP() {
        GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
        this.localPlayer = new EntityPonyModel(profile);
        this.remotePlayer = new EntityPonyModel(profile);
    }

    @Override
    protected void setLocalSkin(File pendingSkin) {
        super.setLocalSkin(pendingSkin);
        MineLPLogger.debug("Invalidating old local skin, checking updated local skin");
        try {
            PonyManager.getInstance().getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).checkSkin(ImageIO.read(pendingSkin));
        } catch (IOException e) {
            MineLPLogger.error(e, "Unable to read file {}", pendingSkin.getName());
        }

    }

    @Override
    protected void setRemoteSkin() {
        super.setRemoteSkin();
        PonyManager.getInstance().getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        PonyManager.getInstance().getPonyFromResourceRegistry(this.localPlayer.getSkinTexture()).invalidateSkinCheck();
        PonyManager.getInstance().getPonyFromResourceRegistry(this.remotePlayer.getSkinTexture()).invalidateSkinCheck();

    }
}
