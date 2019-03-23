package com.minelittlepony.client.render.tileentities.skull;

import com.minelittlepony.client.render.entities.RenderPonySkeleton;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class WitherSkullRenderer extends PonySkull {

    @Override
    public boolean canRender(PonyConfig config) {
        return config.skeletons;
    }

    @Override
    public ResourceLocation getSkinResource(@Nullable GameProfile profile) {
        return RenderPonySkeleton.WITHER;
    }
}
