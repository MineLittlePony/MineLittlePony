package com.minelittlepony.client.hdskins;

import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.client.gui.element.Cycler;
import com.minelittlepony.common.client.gui.style.Style;
import com.minelittlepony.hdskins.client.dummy.PlayerPreview;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.server.SkinServerList;
import com.minelittlepony.hdskins.profile.SkinType;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
class GuiSkinsMineLP extends GuiSkins {

    private IPonyManager ponyManager = MineLittlePony.getInstance().getManager();

    private boolean isWet = false;

    private static final String[] panoramas = new String[] {
        "minelittlepony:textures/cubemap/sugarcubecorner",
        "minelittlepony:textures/cubemap/quillsandsofas",
        "minelittlepony:textures/cubemap/sweetappleacres"
    };

    public GuiSkinsMineLP(Screen parent, SkinServerList servers) {
        super(parent, servers);
    }

    @Override
    public PlayerPreview createPreviewer() {
        return new PonyPreview();
    }

    @Override
    public void init() {
        super.init();

        addButton(new Cycler(width - 25, 142, 20, 20))
                .setStyles(
                        new Style().setIcon(new ItemStack(Items.BUCKET)).setTooltip("minelp.mode.dry", 0, 10),
                        new Style().setIcon(new ItemStack(Items.WATER_BUCKET)).setTooltip("minelp.mode.wet", 0, 10)
                )
                .onChange(this::setWet)
                .setValue(isWet ? 1 : 0);
    }

    @Override
    protected Identifier getBackground() {
        int i = (int)Math.floor(Math.random() * panoramas.length);

        return new Identifier(panoramas[i]);
    }

    protected int setWet(int wet) {
        playSound(SoundEvents.BLOCK_BREWING_STAND_BREW);

        isWet = wet == 1;

        previewer.getLocal().getTextures().release();;

        if (previewer instanceof PonyPreview) {
            ((PonyPreview)previewer).setWet(isWet);
        }
        return wet;
    }

    @Override
    public void onSetLocalSkin(SkinType type) {
        super.onSetLocalSkin(type);

        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        if (type == SkinType.SKIN) {
            ponyManager.removePony(previewer.getLocal().getTextures().get(SkinType.SKIN).getId());
        }
    }

    @Override
    public void onSetRemoteSkin(SkinType type, Identifier location, MinecraftProfileTexture profileTexture) {
        super.onSetRemoteSkin(type, location, profileTexture);

        MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
        if (type == SkinType.SKIN) {
            ponyManager.removePony(location);
        }
    }
}
