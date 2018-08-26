package com.minelittlepony;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.ISkinParser;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class PonySkinParser implements ISkinParser {

    @Override
    public void parse(MinecraftProfileTexture.Type type, ResourceLocation resource, Map<String, String> metadata) {
        if (type == MinecraftProfileTexture.Type.SKIN) {
            boolean slim = "slim".equals(metadata.get("model"));
            // TODO use proper model metadata system

            metadata.put("model", MineLittlePony.getInstance().getManager()
                    .getPony(resource)
                    .getRace(false)
                    .getModel()
                    .getId(slim));

        }
    }
}
