package com.minelittlepony.render.skull;

import com.minelittlepony.PonyConfig;
import com.minelittlepony.render.ponies.RenderPonySkeleton;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SkeletonSkullRenderer extends PonySkull {

    @Override
    public boolean canRender(PonyConfig config) {
        return config.skeletons;
    }

    @Override
    public ResourceLocation getSkinResource(@Nullable GameProfile profile) {
        return RenderPonySkeleton.SKELETON;
    }
}
