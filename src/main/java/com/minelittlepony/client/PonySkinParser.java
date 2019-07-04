package com.minelittlepony.client;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.model.races.PlayerModels;
import com.minelittlepony.common.event.SkinAvailableCallback;
import com.minelittlepony.common.util.ProfileTextureUtil;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PonySkinParser implements SkinAvailableCallback {

    @Override
    public void onSkinAvailable(MinecraftProfileTexture.Type type, Identifier id, MinecraftProfileTexture texture) {

        if (type == MinecraftProfileTexture.Type.SKIN) {

            Map<String, String> metadata = ProfileTextureUtil.getMetadata(texture);
            if (metadata == null) {
                metadata = new HashMap<>();
                ProfileTextureUtil.setMetadata(texture, metadata);
            }
            boolean slim = "slim".equals(metadata.get("model"));

            // TODO use proper model metadata system
            metadata.put("model", PlayerModels.forRace(MineLittlePony.getInstance().getManager()
                    .getPony(id)
                    .getRace(false))
                    .getId(slim));
        }
    }
}
