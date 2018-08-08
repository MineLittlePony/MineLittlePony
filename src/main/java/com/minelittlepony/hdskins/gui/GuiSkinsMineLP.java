package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiItemStackButton;
import com.voxelmodpack.hdskins.gui.GuiSkins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
public class GuiSkinsMineLP extends GuiSkins {

    private PonyManager ponyManager = MineLittlePony.getInstance().getManager();

    private GuiButton btnModeWet;
    private GuiButton btnModeDry;

    private boolean isWet = false;


    private static final String[] panoramas = new String[] {
        "minelp:textures/cubemap/sugarcubecorner_%d.png",
        "minelp:textures/cubemap/quillsandsofas_%d.png"
    };

    @Override
    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPonyModel(profile);
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.add(btnModeWet = new GuiItemStackButton(7, 2, 99, new ItemStack(Items.WATER_BUCKET)));
        buttonList.add(btnModeDry = new GuiItemStackButton(8, 2, 80, new ItemStack(Items.BUCKET)));

        btnModeDry.enabled = isWet;
        btnModeWet.enabled = !isWet;
    }

    @Override
    protected void initPanorama() {
        int i = (int)Math.floor(Math.random() * panoramas.length);

        panorama.setSource(panoramas[i]);
    }


    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);

        if (guiButton.id == this.btnModeDry.id) {
            this.isWet = false;
            this.localPlayer.releaseTextures();
        } else if (guiButton.id == this.btnModeWet.id) {
            this.isWet = true;
            this.localPlayer.releaseTextures();
        }

        btnModeDry.enabled = isWet;
        btnModeWet.enabled = !isWet;

        ((EntityPonyModel)this.localPlayer).setWet(isWet);
        ((EntityPonyModel)this.remotePlayer).setWet(isWet);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        super.drawScreen(mouseX, mouseY, partialTick);

        if (btnModeDry.isMouseOver() || btnModeWet.isMouseOver()) {
            int y = Math.max(mouseY, 16);
            String text;
            if (btnModeDry.isMouseOver()) {
                text = "minelp.mode.dry";
            } else {
                text = "minelp.mode.wet";
            }
            this.drawHoveringText(I18n.format(text), mouseX, y);
        };
    }

    @Override
    protected void onSetLocalSkin(Type type) {
        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        if (type == Type.SKIN) {
            ponyManager.removePony(localPlayer.getLocal(Type.SKIN).getTexture());
        }
    }

    @Override
    protected void onSetRemoteSkin(Type type, ResourceLocation resource, MinecraftProfileTexture profileTexture) {
        MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
        if (type == Type.SKIN) {
            ponyManager.removePony(resource);
        }
    }
}
