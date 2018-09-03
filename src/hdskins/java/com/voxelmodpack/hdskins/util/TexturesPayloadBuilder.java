package com.voxelmodpack.hdskins.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Use this to build a {@link MinecraftTexturesPayload} object. This is
 * required because it has no useful constructor. This uses reflection
 * via Gson to create a new instance and populate the fields.
 */
@SuppressWarnings("unused")
public class TexturesPayloadBuilder {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    public static MinecraftTexturesPayload createTexturesPayload(GameProfile profile, Map<Type, MinecraftProfileTexture> textures) {
        // This worked fine as is before I started using sub-classes.
        MinecraftTexturesPayload payload = gson.fromJson(gson.toJson(new TexturesPayloadBuilder(profile)), MinecraftTexturesPayload.class);
        payload.getTextures().putAll(textures);
        return payload;
    }

    private long timestamp;

    private UUID profileId;
    private String profileName;

    private boolean isPublic;

    private Map<Type, MinecraftProfileTexture> textures;

    private TexturesPayloadBuilder(GameProfile profile) {
        profileId = profile.getId();
        profileName = profile.getName();
        timestamp = System.currentTimeMillis();

        isPublic = true;

        this.textures = new HashMap<>();
    }
}
