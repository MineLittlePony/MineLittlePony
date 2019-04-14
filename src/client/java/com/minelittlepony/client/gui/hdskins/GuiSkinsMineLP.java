package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.common.client.gui.element.IconicToggle;
import com.minelittlepony.common.client.gui.style.Style;
import com.minelittlepony.hdskins.gui.EntityPlayerModel;
import com.minelittlepony.hdskins.gui.GuiSkins;
import com.minelittlepony.hdskins.net.SkinServer;
import com.minelittlepony.pony.IPonyManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
public class GuiSkinsMineLP extends GuiSkins {

    private IPonyManager ponyManager = MineLittlePony.getInstance().getManager();

    private boolean isWet = false;

    private static final String[] panoramas = new String[] {
        "minelittlepony:textures/cubemap/sugarcubecorner",
        "minelittlepony:textures/cubemap/quillsandsofas",
        "minelittlepony:textures/cubemap/sweetappleacres"
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

        addButton(new IconicToggle(width - 25, 142))
                .setStyles(
                        new Style().setIcon(new ItemStack(Items.WATER_BUCKET)).setTooltip("minelp.mode.wet", 0, 10),
                        new Style().setIcon(new ItemStack(Items.BUCKET)).setTooltip("minelp.mode.dry", 0, 10)
                )
                .onChange(this::setWet)
                .setValue(isWet ? 1 : 0);
    }

    @Override
    protected ResourceLocation getBackground() {
        int i = (int)Math.floor(Math.random() * panoramas.length);

        return new ResourceLocation(panoramas[i]);
    }

    protected int setWet(int wet) {
        playSound(SoundEvents.BLOCK_BREWING_STAND_BREW);

        isWet = wet == 1;
        localPlayer.releaseTextures();

        ((EntityPonyModel)localPlayer).setWet(isWet);
        ((EntityPonyModel)remotePlayer).setWet(isWet);

        return wet;
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
