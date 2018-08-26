package com.voxelmodpack.hdskins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * SkinParser is used to parse metadata (e.g. trigger pixels) from a texture.
 */
@FunctionalInterface
public interface ISkinParser {

    /**
     * Parses the texture for metadata. Any discovered data should be put into
     * the metadata Map parameter.
     *
     * @param profile The profile whose skin is being parsed.
     * @param type The texture type
     * @param resource The texture location
     * @param metadata The metadata previously parsed
     */
    void parse(GameProfile profile, Type type, ResourceLocation resource, Map<String, String> metadata);
}
