package com.minelittlepony.api.pony;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.minelittlepony.api.pony.meta.Race;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class DefaultPonySkinHelper {
    public static final Identifier STEVE = Pony.id("textures/entity/player/wide/steve_pony.png");

    public static final Identifier SEAPONY_SKIN_TYPE_ID = Pony.id("seapony");
    public static final Identifier NIRIK_SKIN_TYPE_ID = Pony.id("nirik");

    private static final Function<SkinTextures, SkinTextures> SKINS = Util.memoize(original -> new SkinTextures(
            remapAsset(original.body()),
            null,
            null,
            original.model(),
            false
    ));

    private static TextureAssetInfo remapAsset(TextureAsset asset) {
        Identifier id = Pony.id(asset.texturePath().getPath().replace(".png", "_pony.png"));
        return new TextureAssetInfo(id, id);
    }

    public static SkinTextures getTextures(SkinTextures original) {
        return SKINS.apply(original);
    }

    public static String getModelType(UUID id) {
        SkinTextures textures = DefaultSkinHelper.getSkinTextures(id);
        return getModelType(Pony.getManager().getPony(textures.body().texturePath(), id).race(), textures.model());
    }

    public static String getModelType(Race race, PlayerSkinType armShape) {
        if (race.isHuman()) {
            return armShape.asString();
        }
        return (armShape == PlayerSkinType.SLIM) ? armShape.asString() + race.name().toLowerCase(Locale.ROOT) : race.name().toLowerCase(Locale.ROOT);
    }
}
