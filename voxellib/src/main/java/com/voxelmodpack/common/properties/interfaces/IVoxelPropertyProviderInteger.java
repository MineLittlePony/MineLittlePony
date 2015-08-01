package com.voxelmodpack.common.properties.interfaces;

/**
 * Interface for property providers that can provide storage for integer values
 * 
 * @author Adam Mummery-Smith
 */
public interface IVoxelPropertyProviderInteger extends IVoxelPropertyProvider {
    /**
     * Set the specified property to the integer value specified
     * 
     * @param propertyName
     * @param value
     */
    public abstract void setProperty(String propertyName, int value);

    /**
     * Attempts to return the specified property as an integer by calling
     * Integer.parseInt on the underlying string value
     * 
     * @param propertyName
     * @return
     */
    public abstract int getIntProperty(String propertyName);
}
