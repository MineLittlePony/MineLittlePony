package com.minelittlepony.client.hdskins;

import com.minelittlepony.api.pony.IPonyManager;
import com.minelittlepony.client.GuiPonySettings;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.hdskins.client.dummy.PlayerPreview;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.server.SkinServerList;
import com.minelittlepony.hdskins.profile.SkinType;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import org.lwjgl.glfw.GLFW;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
class GuiSkinsMineLP extends GuiSkins {

    private IPonyManager ponyManager = MineLittlePony.getInstance().getManager();

    private static final String[] PANORAMAS = new String[] {
        "minelittlepony:textures/cubemap/sugarcubecorner",
        "minelittlepony:textures/cubemap/quillsandsofas",
        "minelittlepony:textures/cubemap/sweetappleacres"
    };

    public GuiSkinsMineLP(Screen parent, SkinServerList servers) {
        super(parent, servers);
    }

    @Override
    public void init() {
        super.init();

        if (!(parent instanceof GuiPonySettings)) {
            addButton(new Button(width - 25, height - 90, 20, 20))
                .onClick(sender -> client.setScreen(new GuiPonySettings(this)))
                .getStyle()
                    .setIcon(new TextureSprite()
                            .setPosition(2, 2)
                            .setTexture(new Identifier("minelittlepony", "textures/gui/pony.png"))
                            .setTextureSize(16, 16)
                            .setSize(16, 16))
                    .setTooltip("minelp.options.title", 0, 10);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (modifiers == (GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_CONTROL) && keyCode == GLFW.GLFW_KEY_R) {
            client.reloadResources();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public PlayerPreview createPreviewer() {
        return new PonyPreview();
    }

    @Override
    protected Identifier getBackground() {
        int i = (int)Math.floor(Math.random() * PANORAMAS.length);

        return new Identifier(PANORAMAS[i]);
    }

    @Override
    public void onSetLocalSkin(SkinType type) {
        super.onSetLocalSkin(type);

        MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
        if (type == SkinType.SKIN) {
            previewer.getLocal().ifPresent(local -> ponyManager.removePony(local.getTextures().get(SkinType.SKIN).getId()));
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
