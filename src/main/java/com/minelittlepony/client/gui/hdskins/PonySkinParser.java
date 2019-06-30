package com.minelittlepony.client.gui.hdskins;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.hdskins.ISkinParser;
import com.minelittlepony.hdskins.VanillaModels;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.util.Identifier;

import java.util.Map;

public class PonySkinParser implements ISkinParser {

    @Override
    public void parse(GameProfile profile, MinecraftProfileTexture.Type type, Identifier resource,
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
