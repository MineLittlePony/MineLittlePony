package com.voxelmodpack.hdskins.skins;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSkinServer implements SkinServer {

    private static final Logger logger = LogManager.getLogger();

    protected static final ExecutorService skinDownloadExecutor = Executors.newCachedThreadPool();
    protected static final ListeningExecutorService skinUploadExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

    private LoadingCache<GameProfile, Optional<MinecraftTexturesPayload>> skins = CacheBuilder.newBuilder()
            .initialCapacity(20)
            .maximumSize(100)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build(AsyncCacheLoader.create(new CacheLoader<GameProfile, Optional<MinecraftTexturesPayload>>() {

                @Override
                public Optional<MinecraftTexturesPayload> load(GameProfile key) {
                    Preconditions.checkNotNull(key, "profile cannot be null");
                    // prevent race condition where one server responds faster than the previous one
                    synchronized (key) {
                        return loadProfileData(key);
                    }
                }
            }, Optional.empty(), skinDownloadExecutor));

    protected abstract Optional<MinecraftTexturesPayload> loadProfileData(GameProfile profile);

    @Override
    public final Optional<MinecraftTexturesPayload> getProfileData(GameProfile profile) {
        boolean was = !skins.asMap().containsKey(profile);
        Optional<MinecraftTexturesPayload> textures = skins.getUnchecked(profile);
        // This is the initial value. Refreshing will load it syncronously.
        if (was) {
            skins.refresh(profile);
        }
        return textures;
    }

    @Override
    public void clearCache() {
        skins.invalidateAll();
    }

}
