package com.minelittlepony.client;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.Tooltip;
import com.minelittlepony.common.client.gui.element.Button;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class SkinsProxy {

    public static SkinsProxy instance = new SkinsProxy();

    @Nullable
    public Identifier getSkinTexture(GameProfile profile) {
        PlayerSkinProvider skins = MinecraftClient.getInstance().getSkinProvider();

        @Nullable
        MinecraftProfileTexture texture = skins.getTextures(profile).get(MinecraftProfileTexture.Type.SKIN);

        if (texture == null) {
            return null;
        }

        return skins.loadSkin(texture, MinecraftProfileTexture.Type.SKIN);
    }

    public void renderOption(Screen screen, @Nullable Screen parent, int row, int RIGHT, ScrollContainer content) {
        content.addButton(new Button(RIGHT, row += 20, 150, 20))
            .setEnabled(false)
            .getStyle()
                .setTooltip(Tooltip.of("minelp.options.skins.hdskins.disabled", 200))
                .setText("minelp.options.skins.hdskins.open");
    }

    public Identifier getSeaponySkin(EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager, AbstractClientPlayerEntity player) {
        return manager.getPony(player).getTexture();
    }
}














