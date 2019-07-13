package com.minelittlepony.client.render.tileentities.skull;

import com.minelittlepony.client.render.entities.RenderPonyZombie;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class ZombieSkullRenderer extends PonySkull {

    @Override
    public boolean canRender() {
        return PonyConfig.INSTANCE.zombies.get();
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        return RenderPonyZombie.ZOMBIE;
    }
}
