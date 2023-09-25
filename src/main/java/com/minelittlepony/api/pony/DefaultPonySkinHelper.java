package com.minelittlepony.api.pony;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.Race;

import java.util.*;

public final class DefaultPonySkinHelper {
    public static final Identifier STEVE = new Identifier("minelittlepony", "textures/entity/player/wide/steve_pony.png");

    private static final Map<SkinTextures, SkinTextures> SKINS = new HashMap<>();

    public static SkinTextures getTextures(SkinTextures original) {
        return SKINS.computeIfAbsent(original, o -> {
            return new SkinTextures(
                    new Identifier("minelittlepony", original.texture().getPath().replace(".png", "_pony.png")),
                    null,
                    null,
                    null,
                    original.model(),
                    false
            );
        });
    }

    public static String getModelType(UUID id) {
        SkinTextures textures = DefaultSkinHelper.getTexture(id);
        return getModelType(Pony.getManager().getPony(textures.texture(), id).race(), textures.model());
    }

    public static String getModelType(Race race, SkinTextures.Model armShape) {
        if (race.isHuman()) {
            return armShape.getName();
        }
        return (armShape == SkinTextures.Model.SLIM) ? armShape.getName() + race.name().toLowerCase(Locale.ROOT) : race.name().toLowerCase(Locale.ROOT);
    }
}
