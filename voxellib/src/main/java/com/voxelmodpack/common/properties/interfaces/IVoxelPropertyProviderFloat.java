package com.voxelmodpack.common.properties.interfaces;

/**
 * Interface for property providers that can provide storage for float values
 * 
 * @author Adam Mummery-Smith
 */
public interface IVoxelPropertyProviderFloat extends IVoxelPropertyProvider {
    /**
     * Set the specified property to the value specified
     * 
     * @param propertyName
     * @param value
     */
    public abstract void setProperty(String propertyName, float value);

    /**
     * Attempts to retrieve the specified value as a float by calling
     * Float.parseFloat on the underlying string value
     * 
     * @param propertyName
     * @return
     */
    public abstract float getFloatProperty(String propertyName);
}
