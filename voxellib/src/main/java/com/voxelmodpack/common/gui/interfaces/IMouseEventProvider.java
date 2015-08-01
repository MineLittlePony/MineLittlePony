package com.voxelmodpack.common.gui.interfaces;

/**
 * Interface for screens which can provide mouse events to multiple clients
 * 
 * @author Adam Mummery-Smith
 */
public interface IMouseEventProvider {
    /**
     * Register a new mouse event listener for this provider instance
     */
    public abstract void registerMouseListener(IMouseEventListener listener);
}
