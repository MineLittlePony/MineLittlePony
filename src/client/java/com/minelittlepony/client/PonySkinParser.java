package com.minelittlepony.client;

import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.common.MineLittlePony;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.ISkinParser;
import com.voxelmodpack.hdskins.VanillaModels;

import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class PonySkinParser implements ISkinParser {

    @Override
    public void parse(GameProfile profile, MinecraftProfileTexture.Type type, ResourceLocation resource,
            Map<String, String> metadata) {
        if (type == MinecraftProfileTexture.Type.SKIN) {
            boolean slim = VanillaModels.isSlim(metadata.get("model"));
            // TODO use proper model metadata system

            metadata.put("model", PlayerModels.forRace(MineLittlePony.getInstance().getManager()
                    .getPony(resource, profile.getId())
                    .getRace(false))
                    .getId(slim));

        }
    }
}
