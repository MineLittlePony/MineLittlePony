package com.minelittlepony.api.pony;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import com.minelittlepony.api.pony.meta.Race;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class DefaultPonySkinHelper {
    public static final Identifier STEVE = Pony.id("textures/entity/player/wide/steve_pony.png");

    public static final Identifier SEAPONY_SKIN_TYPE_ID = Pony.id("seapony");
    public static final Identifier NIRIK_SKIN_TYPE_ID = Pony.id("nirik");

    private static final Function<PlayerSkin, PlayerSkin> SKINS = Util.memoize(original -> new PlayerSkin(
            remapAsset(original.body()),
            null,
            null,
            original.model(),
            false
    ));

    private static ClientAsset.Texture remapAsset(ClientAsset.Texture asset) {
        Identifier id = Pony.id(asset.texturePath().getPath().replace(".png", "_pony.png"));
        return new ClientAsset.ResourceTexture(id, id);
    }

    public static PlayerSkin getTextures(PlayerSkin original) {
        return SKINS.apply(original);
    }

    public static String getModelType(UUID id) {
        PlayerSkin textures = DefaultPlayerSkin.get(id);
        return getModelType(Pony.getManager().getPony(textures.body().texturePath(), id).race(), textures.model());
    }

    public static String getModelType(Race race, PlayerModelType armShape) {
        if (race.isHuman()) {
            return armShape.getSerializedName();
        }
        return (armShape == PlayerModelType.SLIM) ? armShape.getSerializedName() + race.name().toLowerCase(Locale.ROOT) : race.name().toLowerCase(Locale.ROOT);
    }
}
