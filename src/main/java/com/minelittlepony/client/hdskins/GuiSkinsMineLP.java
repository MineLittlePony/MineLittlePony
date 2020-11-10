package com.minelittlepony.client.hdskins;

import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.hdskins.client.dummy.PlayerPreview;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.server.SkinServerList;
import com.minelittlepony.hdskins.profile.SkinType;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
class GuiSkinsMineLP extends GuiSkins {

    private IPonyManager ponyManager = MineLittlePony.getInstance().getManager();

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
    protected Identifier getBackground() {
        int i = (int)Math.floor(Math.random() * panoramas.length);

        return new Identifier(panoramas[i]);
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
