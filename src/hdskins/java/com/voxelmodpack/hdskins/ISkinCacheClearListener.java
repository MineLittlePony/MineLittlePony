package com.voxelmodpack.hdskins;

/**
 * Callback to perfom additional actions when the skin cache is cleared.
 */
@FunctionalInterface
public interface ISkinCacheClearListener {
    boolean onSkinCacheCleared();
}
