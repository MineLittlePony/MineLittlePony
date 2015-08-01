package com.voxelmodpack.common.interfaces;

/**
 * @author Adam Mummery-Smith
 */
public interface INotifyable {
    /**
     * @param message
     * @param params
     */
    public abstract void notify(String message, Object... params);
}
