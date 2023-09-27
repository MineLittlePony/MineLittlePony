package com.minelittlepony.client.compat.hdskins;

import com.minelittlepony.client.PonySettingsScreen;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.sprite.TextureSprite;
import com.minelittlepony.hdskins.client.gui.DualCarouselWidget;
import com.minelittlepony.hdskins.client.gui.GuiSkins;
import com.minelittlepony.hdskins.server.SkinServerList;
import com.minelittlepony.hdskins.profile.SkinType;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

/**
 * Skin uploading GUI. Usually displayed over the main menu.
 */
class GuiSkinsMineLP extends GuiSkins {
    private static final String[] PANORAMAS = new String[] {
        "minelittlepony:textures/cubemap/sugarcubecorner",
        "minelittlepony:textures/cubemap/quillsandsofas",
        "minelittlepony:textures/cubemap/sweetappleacres"
    };

    public GuiSkinsMineLP(Screen parent, SkinServerList servers) {
        super(parent, servers);
        chooser.addSkinChangedEventListener(type -> {
            MineLittlePony.logger.debug("Invalidating old local skin, checking updated local skin");
            if (type == SkinType.SKIN) {
                MineLittlePony.getInstance().getManager().removePony(previewer.getLocal().getSkins().get(SkinType.SKIN).getId());
            }
        });
        uploader.addSkinLoadedEventListener((type, location, profileTexture) -> {
            MineLittlePony.logger.debug("Invalidating old remote skin, checking updated remote skin");
            if (type == SkinType.SKIN) {
                MineLittlePony.getInstance().getManager().removePony(location);
            }
        });
    }

    @Override
    protected void initServerPreviewButtons(Bounds area) {
        if (!(parent instanceof PonySettingsScreen)) {
            addButton(new Button(area.right() - 20, area.bottom() + 5, 20, 20))
                .onClick(sender -> client.setScreen(new PonySettingsScreen(this)))
                .getStyle()
                    .setIcon(new TextureSprite()
                            .setPosition(2, 2)
                            .setTexture(new Identifier("minelittlepony", "textures/gui/pony.png"))
                            .setTextureSize(16, 16)
                            .setSize(16, 16))
                    .setTooltip("minelp.options.title", 0, 10);
            super.initServerPreviewButtons(new Bounds(area.top, area.left, area.width - 25, area.height));
        } else {
            super.initServerPreviewButtons(area);
        }
    }

    @Override
    public DualCarouselWidget createPreviewer() {
        return new PonifiedDualCarouselWidget(this);
    }

    @Override
    protected Identifier getBackground() {
        int i = (int)Math.floor(Math.random() * PANORAMAS.length);

        return new Identifier(PANORAMAS[i]);
    }
}
