package com.voxelmodpack.common.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderBoolean;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderFloat;
import com.voxelmodpack.common.properties.interfaces.IVoxelPropertyProviderInteger;

/**
 * Configuration file for VoxelMods
 */
public abstract class ModConfig
        implements IVoxelPropertyProviderFloat, IVoxelPropertyProviderInteger, IVoxelPropertyProviderBoolean {
    /**
     * Set of default properties Set these in setDefaults
     */
    protected final Properties defaults = new Properties();

    /**
     * The runtime configuration
     */
    protected Properties config;

    /**
     * name of the mod that this belongs to
     */
    protected final String modName;

    /**
     * String path to the config file
     */
    protected final String propertiesFileName;

    /**
     * File that the configuration is held in
     */
    protected File propertiesFile;

    /**
     * @param modName Name of the mod that this will belong to
     * @param propertiesFileName string path to the file that this uses
     */
    public ModConfig(String modName, String propertiesFileName) {
        this.modName = modName;
        this.propertiesFileName = propertiesFileName;
        this.setDefaults();
        LiteLoaderLogger.info("%s> Attempting to load/create the configuration.", this.modName);
        this.loadConfig();
    }

    /**
     * Use this to set the defaults in a config
     */
    protected abstract void setDefaults();

    /**
     * Attemps to load the saved config file If the file cannot load the
     * defaults are used If the file does not exist one is created with the
     * defaults saved to it
     */
    protected void loadConfig() {
        this.config = new Properties(this.defaults);

        try {
            this.propertiesFile = new File(LiteLoader.getCommonConfigFolder(), this.propertiesFileName);

            if (this.propertiesFile.exists()) {
                LiteLoaderLogger.info("%s> Config file found, loading...", this.modName);
                this.config.load(new FileInputStream(this.propertiesFile));
            } else {
                LiteLoaderLogger.info("%s> No config file found, creating...", this.modName);
                this.createConfig();
            }
        } catch (Exception ex) {
            LiteLoaderLogger.warning("%s> ERROR: %s", this.modName, ex.toString());
        }
    }

    /**
     * Create the config Only used to generate a new config file
     */
    protected void createConfig() {
        try {
            this.config.putAll(this.defaults);
            this.config.store(new FileWriter(this.propertiesFile), null);
        } catch (Exception ex) {
            LiteLoaderLogger.warning("%s> ERROR: %s", this.modName, ex.toString());
        }
    }

    /**
     * Set the given property to a float
     * 
     * @param propertyName The property to change
     * @param s The float value to set it to
     */
    @Override
    public void setProperty(String propertyName, float value) {
        this.config.setProperty(propertyName, String.valueOf(value));
        this.saveConfig();
    }

    /**
     * Set the given property to a int
     * 
     * @param propertyName The property to change
     * @param s The int value to set it to
     */
    @Override
    public void setProperty(String propertyName, int value) {
        this.config.setProperty(propertyName, String.valueOf(value));
        this.saveConfig();
    }

    /**
     * Set the given property to a boolean
     * 
     * @param propertyName The property to change
     * @param s The boolean value to set it to
     */
    @Override
    public void setProperty(String propertyName, boolean value) {
        this.config.setProperty(propertyName, String.valueOf(value));
        this.saveConfig();
    }

    /**
     * Set the given property to a string
     * 
     * @param propertyName The property to change
     * @param value The String to set it to
     */
    public void setProperty(String propertyName, String value) {
        this.config.setProperty(propertyName, value);
        this.saveConfig();
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A string value of the given property
     */
    @Override
    public String getStringProperty(String propertyName) {
        return this.config.getProperty(propertyName);
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A float value of the given property
     */
    @Override
    public float getFloatProperty(String propertyName) {
        return Float.parseFloat(this.config.getProperty(propertyName));
    }

    /**
     * @param propertyName Name of the Property to get
     * @param min the minimum that the property can be
     * @param max the maximum that the property can be
     * @return a float representation of the property that is within the min and
     *         max
     */
    public float getClampedFloatProperty(String propertyName, float min, float max) {
        float value = this.getFloatProperty(propertyName);
        return Math.min(Math.max(value, min), max);
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A int value of the given property
     */
    @Override
    public int getIntProperty(String propertyName) {
        return Integer.parseInt(this.config.getProperty(propertyName));
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A boolean value of the given property
     */
    @Override
    public boolean getBoolProperty(String propertyName) {
        return Boolean.parseBoolean(this.config.getProperty(propertyName));
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A int value of the default for the given property
     */
    @Override
    public String getDefaultPropertyValue(String propertyName) {
        return this.defaults.getProperty(propertyName);
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A float value of the default for the given property
     */
    public float getDefaultFloatProperty(String propertyName) {
        return Float.parseFloat(this.defaults.getProperty(propertyName));
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A int value of the default for the given property
     */
    public int getDefaultIntProperty(String propertyName) {
        return Integer.parseInt(this.defaults.getProperty(propertyName));
    }

    /**
     * @param propertyName The property that gets it's value returned
     * @return A boolean value of the default for the given property
     */
    public boolean getDefaultBoolProperty(String propertyName) {
        return Boolean.parseBoolean(this.defaults.getProperty(propertyName));
    }

    /**
     * Saves the configuration to file
     */
    public void saveConfig() {
        try {
            this.config.store(new FileWriter(this.propertiesFile), null);
        } catch (Exception e) {
            LiteLoaderLogger.warning("%s> ERROR: %s", this.modName, e.toString());
        }
    }

    /**
     * Swaps the state of a boolean option
     */
    @Override
    public void toggleOption(String propertyName) {
        this.setProperty(propertyName, !this.getBoolProperty(propertyName));
    }

    /**
     * 
     */
    @Override
    public String getOptionDisplayString(String propertyName) {
        return "";
    }
}
