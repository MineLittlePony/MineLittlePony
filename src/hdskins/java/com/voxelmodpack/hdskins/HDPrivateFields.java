package com.voxelmodpack.hdskins;

import java.util.Map;

import com.google.common.cache.Cache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.PrivateFields;

import net.minecraft.client.resources.SkinManager;

public class HDPrivateFields<P, T> extends PrivateFields<P, T> {

    public static PrivateFields<SkinManager, Cache<GameProfile, Map<Type, MinecraftProfileTexture>>> skinCacheLoader = new HDPrivateFields<SkinManager, Cache<GameProfile, Map<Type, MinecraftProfileTexture>>>(
            SkinManager.class, HDObf.skinLoadingCache);

    protected HDPrivateFields(Class<P> owner, Obf obf) {
        super(owner, obf);
    }

    private static class HDObf extends Obf {
        private static Obf skinLoadingCache = new HDObf("field_152798_f", "e", "skinCacheLoader");

        protected HDObf(String seargeName, String obfName, String mcpName) {
            super(seargeName, obfName, mcpName);
        }
    }

}
