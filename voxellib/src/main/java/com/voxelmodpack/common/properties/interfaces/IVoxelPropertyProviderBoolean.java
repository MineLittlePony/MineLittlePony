package com.voxelmodpack.common.properties.interfaces;

/**
 * Interface for property providers that can provide boolean values
 * 
 * @author Adam Mummery-Smith
 */
public interface IVoxelPropertyProviderBoolean extends IVoxelPropertyProvider {
    /**
     * Set the specified property to the boolean value specified
     * 
     * @param propertyName
     * @param value
     */
    public abstract void setProperty(String propertyName, boolean value);

    /**
     * Attempts to parse the value of the property specified by calling
     * Boolean.parseBoolean on the underlying string value
     * 
     * @param propertyName
     * @return
     */
    public abstract boolean getBoolProperty(String propertyName);
}
