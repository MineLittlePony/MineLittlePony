package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.minelittlepony.gui.Button;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiItemStackButton;
import com.voxelmodpack.hdskins.gui.GuiSkins;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
public class GuiSkinsMineLP extends GuiSkins {

    private PonyManager ponyManager = MineLittlePony.getInstance().getManager();

    private Button btnModeWet;
    private Button btnModeDry;

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

        addButton(btnModeWet = new GuiItemStackButton(2, 99, new ItemStack(Items.WATER_BUCKET), sender -> {
            setWet(false);
        })).setTooltip("minelp.mode.wet");
        addButton(btnModeDry = new GuiItemStackButton(2, 80, new ItemStack(Items.BUCKET), sender -> {
            setWet(true);
        })).setTooltip("minelp.mode.dry");

        setWet(false);
    }

    @Override
    protected void initPanorama() {
        int i = (int)Math.floor(Math.random() * panoramas.length);

        panorama.setSource(panoramas[i]);
    }

    protected void setWet(boolean wet) {
        isWet = wet;
        localPlayer.releaseTextures();

        btnModeDry.enabled = isWet;
        btnModeWet.enabled = !isWet;

        ((EntityPonyModel)localPlayer).setWet(isWet);
        ((EntityPonyModel)remotePlayer).setWet(isWet);
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
