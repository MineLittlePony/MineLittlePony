package com.mumfrey.liteloader.modconfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;

/**
 * Registry where we keep the mod config panel classes and config file writers
 *
 * @author Adam Mummery-Smith
 */
public class ConfigManager
{
    /**
     * Mod config panel classes
     */
    private Map<Class<? extends LiteMod>, Class<? extends ConfigPanel>> configPanels = Maps.newHashMap();
    
    /**
     * Mod config writers
     */
    private Map<Exposable, ExposableConfigWriter> configWriters = new HashMap<Exposable, ExposableConfigWriter>();

    /**
     * List of config writers, for faster iteration in onTick
     */
    private List<ExposableConfigWriter> configWriterList = new LinkedList<ExposableConfigWriter>();

    /**
     * Register a mod, adds the config panel class to the map if the mod
     * implements Configurable
     */
    public void registerMod(LiteMod mod)
    {
        if (mod instanceof Configurable)
        {
            Class<? extends ConfigPanel> panelClass = ((Configurable)mod).getConfigPanelClass();
            if (panelClass != null) this.configPanels.put(mod.getClass(), panelClass);
        }

        this.registerExposable(mod, null, false);
    }

    /**
     * @param exposable
     * @param fallbackFileName
     * @param ignoreMissingConfigAnnotation
     */
    public void registerExposable(Exposable exposable, String fallbackFileName, boolean ignoreMissingConfigAnnotation)
    {
        ExposableOptions options = exposable.getClass().<ExposableOptions>getAnnotation(ExposableOptions.class);
        if (options != null)
        {
            if (fallbackFileName == null) fallbackFileName = options.filename();
            this.initConfigWriter(exposable, fallbackFileName, options.strategy(), options.aggressive());
        }
        else if (ignoreMissingConfigAnnotation)
        {
            this.initConfigWriter(exposable, fallbackFileName, ConfigStrategy.Versioned, false);
        }
    }

    /**
     * Create a config writer instance for the specified mod
     * 
     * @param exposable
     * @param fileName
     * @param strategy
     */
    private void initConfigWriter(Exposable exposable, String fileName, ConfigStrategy strategy, boolean aggressive)
    {
        if (this.configWriters.containsKey(exposable))
        {
            return;
        }

        if (Strings.isNullOrEmpty(fileName))
        {
            fileName = exposable.getClass().getSimpleName().toLowerCase();

            if (fileName.startsWith("litemod"))
            {
                fileName = fileName.substring(7);
            }
        }

        ExposableConfigWriter configWriter = ExposableConfigWriter.create(exposable, strategy, fileName, aggressive);
        if (configWriter != null)
        {
            this.configWriters.put(exposable, configWriter);
            this.configWriterList.add(configWriter);
        }
    }

    /**
     * If the specified mod has a versioned config strategy, attempt to copy the
     * config.
     * 
     * @param mod
     * @param newConfigPath
     * @param oldConfigPath
     */
    public void migrateModConfig(LiteMod mod, File newConfigPath, File oldConfigPath)
    {
        if (this.configWriters.containsKey(mod))
        {
            ExposableConfigWriter writer = this.configWriters.get(mod);
            if (writer.isVersioned())
            {
                File newConfigFile = writer.getConfigFile();
                File legacyConfigFile = new File(oldConfigPath, newConfigFile.getName());

                if (legacyConfigFile.exists() && !newConfigFile.exists())
                {
                    try
                    {
                        Files.copy(legacyConfigFile, newConfigFile);
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Check whether a config panel is available for the specified class
     * 
     * @param modClass
     */
    public boolean hasPanel(Class<? extends LiteMod> modClass)
    {
        return modClass != null && this.configPanels.containsKey(modClass);
    }

    /**
     * Instance a new config panel for the specified mod class if one is
     * available.
     * 
     * @param modClass
     */
    public ConfigPanel getPanel(Class<? extends LiteMod> modClass)
    {
        if (modClass != null && this.configPanels.containsKey(modClass))
        {
            try
            {
                return this.configPanels.get(modClass).newInstance();
            }
            catch (InstantiationException ex) {}
            catch (IllegalAccessException ex) {}

            // If instantiation fails, remove the panel
            this.configPanels.remove(modClass);
        }

        return null;
    }

    /**
     * Initialise the config writer for the specified mod
     * 
     * @param exposable
     */
    public void initConfig(Exposable exposable)
    {
        if (this.configWriters.containsKey(exposable))
        {
            this.configWriters.get(exposable).init();
        }
    }

    /**
     * Invalidate the specified mod config, cause it to be written to disk or
     * scheduled for writing if it has been written recently.
     * 
     * @param exposable
     */
    public void invalidateConfig(Exposable exposable)
    {
        if (this.configWriters.containsKey(exposable))
        {
            this.configWriters.get(exposable).invalidate();
        }
    }

    /**
     * Tick all of the configuration writers, handles latent writes for
     * anti-hammer strategy.
     */
    public void onTick()
    {
        for (ExposableConfigWriter writer : this.configWriterList)
        {
            writer.onTick();
        }
    }

    /**
     * Force all mod configs to be flushed to disk
     */
    public void syncConfig()
    {
        for (ExposableConfigWriter writer : this.configWriterList)
        {
            writer.sync();
        }
    }

    /**
     * @param exposable
     */
    public static ConfigStrategy getConfigStrategy(Exposable exposable)
    {
        ExposableOptions options = exposable.getClass().<ExposableOptions>getAnnotation(ExposableOptions.class);
        if (options != null)
        {
            return options.strategy();
        }

        return ConfigStrategy.Unversioned;
    }
}
