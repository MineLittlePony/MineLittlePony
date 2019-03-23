package com.minelittlepony.render.skull;

import com.minelittlepony.PonyConfig;
import com.minelittlepony.render.ponies.RenderPonyZombie;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ZombieSkullRenderer extends PonySkull {

    @Override
    public boolean canRender(PonyConfig config) {
        return config.zombies;
    }

    @Override
    public ResourceLocation getSkinResource(@Nullable GameProfile profile) {
        return RenderPonyZombie.ZOMBIE;
    }
}
