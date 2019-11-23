package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.client.render.entity.MobRenderers;
import com.minelittlepony.client.render.entity.RenderPonySkeleton;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class SkeletonSkullRenderer extends PonySkull {

    @Override
    public boolean canRender(PonyConfig config) {
        return MobRenderers.SKELETONS.get();
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        return RenderPonySkeleton.SKELETON;
    }
}
