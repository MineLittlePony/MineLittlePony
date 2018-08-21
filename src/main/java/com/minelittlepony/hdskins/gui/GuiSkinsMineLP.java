package com.minelittlepony.hdskins.gui;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.PonyManager;
import com.minelittlepony.gui.Button;
import com.minelittlepony.gui.IconicButton;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

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

    public GuiSkinsMineLP(List<SkinServer> servers) {
        super(servers);
    }

    @Override
    protected EntityPlayerModel getModel(GameProfile profile) {
        return new EntityPonyModel(profile);
    }

    @Override
    public void initGui() {
        super.initGui();

        addButton(btnModeWet = new IconicButton(width - 25, 137, sender -> {
            setWet(true);
        }).setIcon(new ItemStack(Items.WATER_BUCKET))).setEnabled(!isWet).setTooltip("minelp.mode.wet").setTooltipOffset(0, 10);

        addButton(btnModeDry = new IconicButton(width - 25, 118, sender -> {
            setWet(false);
        }).setIcon(new ItemStack(Items.BUCKET))).setEnabled(isWet).setTooltip("minelp.mode.dry").setTooltipOffset(0, 10);
    }

    @Override
    protected void initPanorama() {
        int i = (int)Math.floor(Math.random() * panoramas.length);

        panorama.setSource(panoramas[i]);
    }

    protected void setWet(boolean wet) {
        if (wet == isWet) {
            return;
        }

        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_BREWING_STAND_BREW, 1));

        isWet = wet;
        localPlayer.releaseTextures();

        btnModeDry.enabled = isWet;
        btnModeWet.enabled = !isWet;

        ((EntityPonyModel)localPlayer).setWet(isWet);
        ((EntityPonyModel)remotePlayer).setWet(isWet);
    }

    @Override
    public void onSetLocalSkin(Type type) {
        super.onSetLocalSkin(type);
        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        if (type == Type.SKIN) {
            ponyManager.removePony(localPlayer.getLocal(Type.SKIN).getTexture());
        }
    }

    @Override
    public void onSetRemoteSkin(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
        super.onSetRemoteSkin(type, location, profileTexture);

        MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
        if (type == Type.SKIN) {
            ponyManager.removePony(location);
        }
    }
}
