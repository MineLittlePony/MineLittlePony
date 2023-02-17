package com.minelittlepony.api.pony;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.config.PonyLevel;
import com.minelittlepony.client.MineLittlePony;

import java.util.*;

public final class DefaultPonySkinHelper {
    public static final Identifier STEVE = new Identifier("minelittlepony", "textures/entity/player/wide/steve_pony.png");
    @Deprecated
    public static final Identifier ALEX = new Identifier("minelittlepony", "textures/entity/player/slim/alex_pony.png");

    public static Identifier getPonySkin(Identifier original) {
        return original.getPath().contains("steve") ? STEVE : ALEX;
    }

    @Deprecated
    public static Identifier getPonySkin(UUID profileId, boolean slimArms) {
        if (MineLittlePony.getInstance().getConfig().ponyLevel.get() != PonyLevel.PONIES) {
            return DefaultSkinHelper.getTexture(profileId);
        }
        boolean alex = (profileId.hashCode() & 1) == 1;
        return new Identifier("minelittlepony", "textures/entity/player/" + (slimArms ? "slim" : "wide") + "/" + (alex ? "alex" : "steve") + "_pony.png");
    }
}
