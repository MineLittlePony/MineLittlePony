package com.minelittlepony.client.render.tileentities.skull;

import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.model.components.ModelDeadMau5Ears;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class PlayerSkullRenderer extends PonySkull {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    @Override
    public boolean canRender(PonyConfig config) {
        return config.getPonyLevel() != PonyLevel.HUMANS;
    }

    @Override
    public void preRender(boolean transparency) {
        GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);

        if (!transparency) {
            RenderPony.enableModelRenderProfile();
        }
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        deadMau5.setVisible(profile != null && "deadmau5".equals(profile.getName()));

        if (profile != null) {

            Identifier skin = SkinsProxy.instance.getSkinTexture(profile);
            if (skin != null) {
                return skin;
            }
            return DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(profile));
        }

        return DefaultSkinHelper.getTexture();
    }

    @Override
    public void render(float animateTicks, float rotation, float scale) {
        super.render(animateTicks, rotation, scale);
        /*render*/
        deadMau5.render(animateTicks, 0, 0, rotation, 0, scale);
    }
}
