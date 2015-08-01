package com.voxelmodpack.common.properties.interfaces;

/**
 * Base interface for property providers
 *
 * @author Adam Mummery-Smith
 */
public interface IVoxelPropertyProvider {
    /**
     * Get the value of a property as a string
     * 
     * @param propertyName
     * @return
     */
    public abstract String getStringProperty(String propertyName);

    /**
     * Get the name of this property for display
     * 
     * @param propertyName
     * @return
     */
    public abstract String getOptionDisplayString(String propertyName);

    /**
     * Toggle the value of this option
     * 
     * @param propertyName
     */
    public abstract void toggleOption(String propertyName);

    /**
     * Get the default value of the property as a string
     * 
     * @param propertyName
     * @return
     */
    public abstract String getDefaultPropertyValue(String propertyName);
}