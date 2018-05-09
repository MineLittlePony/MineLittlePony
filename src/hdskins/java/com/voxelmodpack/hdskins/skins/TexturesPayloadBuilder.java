package com.voxelmodpack.hdskins.skins;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;

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

    private long timestamp;
    private UUID profileId;
    private String profileName;
    private boolean isPublic;
    private Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;

    public TexturesPayloadBuilder timestamp(long time) {
        this.timestamp = time;
        return this;
    }

    public TexturesPayloadBuilder profileId(UUID uuid) {
        this.profileId = uuid;
        return this;
    }

    public TexturesPayloadBuilder profileName(String name) {
        this.profileName = name;
        return this;
    }

    public TexturesPayloadBuilder isPublic(boolean pub) {
        this.isPublic = pub;
        return this;
    }

    public TexturesPayloadBuilder texture(MinecraftProfileTexture.Type type, MinecraftProfileTexture texture) {
        if (textures == null) textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
        this.textures.put(type, texture);
        return this;
    }

    public TexturesPayloadBuilder textures(Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures) {
        this.textures = textures;
        return this;
    }

    public MinecraftTexturesPayload build() {
        return gson.fromJson(gson.toJson(this), MinecraftTexturesPayload.class);
    }


}
