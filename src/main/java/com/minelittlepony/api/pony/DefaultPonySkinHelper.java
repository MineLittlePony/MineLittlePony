package com.minelittlepony.api.pony;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class DefaultPonySkinHelper {
    public static final Identifier STEVE = new Identifier("minelittlepony", "textures/entity/player/wide/steve_pony.png");

    private static final Map<Identifier, Identifier> SKINS = new HashMap<>();

    public static Identifier getPonySkin(Identifier original) {
        return SKINS.computeIfAbsent(original, DefaultPonySkinHelper::computePonySkin);
    }

    private static Identifier computePonySkin(Identifier original) {
        return new Identifier("minelittlepony", original.getPath().replace(".png", "_pony.png"));
    }
}
