package com.voxelmodpack.hdskins.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class SkinResourceManager implements IResourceManagerReloadListener {

    private Map<UUID, Skin> uuidSkins = Maps.newHashMap();
    private Map<String, Skin> namedSkins = Maps.newHashMap();
    private Map<ResourceLocation, SkinThread> converted = Maps.newHashMap();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        try {
            uuidSkins.clear();
            namedSkins.clear();
            for (SkinThread loc : converted.values()) {
                loc.deleteTexture();
            }
            converted.clear();
            for (IResource res : resourceManager.getAllResources(new ResourceLocation("hdskins", "textures/skins/skins.json"))) {
                try {
                    SkinData data = getSkinData(res.getInputStream());
                    for (Skin s : data.skins) {
                        if (s.uuid != null) {
                            uuidSkins.put(s.uuid, s);
                        }
                        if (s.name != null) {
                            namedSkins.put(s.name, s);
                        }
                    }
                } catch (JsonParseException je) {
                    LiteLoaderLogger.warning(je, "Invalid skins.json in %s", res.getResourcePackName());
                }
            }
        } catch (IOException e) {
            // ignore
        }

    }

    private SkinData getSkinData(InputStream stream) {
        try {
            return new Gson().fromJson(new InputStreamReader(stream), SkinData.class);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Nullable
    public ResourceLocation getPlayerTexture(GameProfile profile, Type type) {
        if (type != Type.SKIN)
            // not supported
            return null;

        Skin skin = getSkin(profile);
        if (skin != null) {
            ResourceLocation res = skin.getTexture();
            if (res != null) {
                SkinThread conv = this.converted.get(res);
                if (conv == null) {
                    // read and convert in a new thread
                    this.converted.put(res, conv = new SkinThread(res));
                }
                // gotta stay in this thread to load it
                if (conv.isReady()) {
                    conv.uploadSkin();
                }
                return conv.getResource();
            }
        }
        return null;
    }

    @Nullable
    private Skin getSkin(GameProfile profile) {
        Skin skin = this.uuidSkins.get(profile.getId());
        if (skin == null) {
            skin = this.namedSkins.get(profile.getName());
        }
        return skin;
    }

}
