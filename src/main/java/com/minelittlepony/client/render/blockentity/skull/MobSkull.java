package com.minelittlepony.client.render.blockentity.skull;

import com.minelittlepony.client.render.MobRenderers;
import com.minelittlepony.settings.PonyConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class MobSkull extends AbstractPonySkull {

    private final Identifier texture;
    private final MobRenderers type;

    MobSkull(Identifier texture, MobRenderers type) {
        this.texture = texture;
        this.type = type;
    }

    @Override
    public boolean canRender(PonyConfig config) {
        return config.ponyskulls.get() && type.get();
    }

    @Override
    public Identifier getSkinResource(@Nullable GameProfile profile) {
        return texture;
    }
}
