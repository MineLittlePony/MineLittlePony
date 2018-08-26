package com.minelittlepony.render.skull;

import com.minelittlepony.PonyConfig;
import com.minelittlepony.model.components.ModelDeadMau5Ears;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.PonySkull;
import com.minelittlepony.render.RenderPony;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import javax.annotation.Nullable;

public class PlayerSkullRenderer extends PonySkull {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    @Override
    public boolean canRender(PonyConfig config) {
        return config.getPonyLevel() != PonyLevel.HUMANS;
    }

    @Override
    public void preRender(boolean transparency) {
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        if (!transparency) {
            RenderPony.enableModelRenderProfile();
        }
    }

    @Override
    public ResourceLocation getSkinResource(@Nullable GameProfile profile) {
        if (profile != null) {
            deadMau5.setVisible("deadmau5".equals(profile.getName()));

            ResourceLocation skin = HDSkinManager.INSTANCE.getTextures(profile).get(MinecraftProfileTexture.Type.SKIN);
            if (skin != null) {
                return skin;
            }

            Minecraft minecraft = Minecraft.getMinecraft();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                return minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            } else {
                return DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));
            }
        }

        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    @Override
    public void render(float animateTicks, float rotation, float scale) {
        super.render(animateTicks, rotation, scale);
        deadMau5.render(null, animateTicks, 0, 0, rotation, 0, scale);
    }
}
