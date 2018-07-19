package com.voxelmodpack.hdskins;

/**
 * Callback for when a skin is loaded.
 *
 */
@FunctionalInterface
public interface ISkinAvailableCallback {
    /**
     * Called when a skin loads.
     */
    void skinAvailable();
}
