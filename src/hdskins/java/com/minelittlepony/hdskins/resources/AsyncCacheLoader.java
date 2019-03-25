package com.minelittlepony.hdskins.resources;

import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.util.Map;
import java.util.concurrent.Executor;

public class AsyncCacheLoader<K, V> extends CacheLoader<K, V> {

    public static <K, V> AsyncCacheLoader<K, V> create(CacheLoader<K, V> loader, V placeholder, Executor executor) {
        return new AsyncCacheLoader<>(loader, placeholder, executor);
    }

    private final CacheLoader<K, V> loader;
    private final V placeholder;
    private final Executor executor;

    private AsyncCacheLoader(CacheLoader<K, V> loader, V placeholder, Executor executor) {
        this.executor = executor;
        this.placeholder = placeholder;
        this.loader = loader;
    }

    @Override
    public V load(K key) {
        return placeholder;
    }

    @Override
    public ListenableFuture<V> reload(final K key, final V oldValue) {
        ListenableFutureTask<V> task = ListenableFutureTask.create(() -> loader.reload(key, oldValue).get());
        executor.execute(task);
        return task;
    }

    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
        return loader.loadAll(keys);
    }
}
