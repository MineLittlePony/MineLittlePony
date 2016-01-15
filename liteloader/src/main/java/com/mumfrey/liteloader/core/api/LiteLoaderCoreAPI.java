package com.mumfrey.liteloader.core.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;

import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.MixinConfigProvider;
import com.mumfrey.liteloader.core.LiteLoaderVersion;
import com.mumfrey.liteloader.interfaces.ObjectFactory;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * LiteLoader's API impl.
 * 
 * @author Adam Mummery-Smith
 */
public abstract class LiteLoaderCoreAPI implements LiteAPI, MixinConfigProvider
{
    protected static final String PKG_LITELOADER = "com.mumfrey.liteloader";
    protected static final String PKG_LITELOADER_COMMON = LiteLoaderCoreAPI.PKG_LITELOADER + ".common";

    protected LoaderEnvironment environment;

    protected LoaderProperties properties;

    protected boolean searchClassPath;
    protected boolean searchModsFolder;

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getIdentifier()
     */
    @Override
    public String getIdentifier()
    {
        return "liteloader";
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getName()
     */
    @Override
    public String getName()
    {
        return "LiteLoader core API";
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getVersion()
     */
    @Override
    public String getVersion()
    {
        return LiteLoaderVersion.CURRENT.getLoaderVersion();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getRevision()
     */
    @Override
    public int getRevision()
    {
        return LiteLoaderVersion.CURRENT.getLoaderRevision();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getModClassPrefix()
     */
    @Override
    public String getModClassPrefix()
    {
        return "LiteMod";
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#init(
     *      com.mumfrey.liteloader.launch.LoaderEnvironment,
     *      com.mumfrey.liteloader.launch.LoaderProperties)
     */
    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.environment = environment;
        this.properties = properties;
    }

    /**
     * Get the discovery settings from the properties file
     */
    void readDiscoverySettings()
    {
        this.searchModsFolder       = this.properties.getAndStoreBooleanProperty(LoaderProperties.OPTION_SEARCH_MODS,      true);
        this.searchClassPath        = this.properties.getAndStoreBooleanProperty(LoaderProperties.OPTION_SEARCH_CLASSPATH, true);

        if (!this.searchModsFolder && !this.searchClassPath)
        {
            LiteLoaderLogger.warning("Invalid configuration, no search locations defined. Enabling all search locations.");

            this.searchModsFolder = true;
            this.searchClassPath  = true;
        }
    }

    /**
     * Write settings
     */
    void writeDiscoverySettings()
    {
        this.properties.setBooleanProperty(LoaderProperties.OPTION_SEARCH_MODS,      this.searchModsFolder);
        this.properties.setBooleanProperty(LoaderProperties.OPTION_SEARCH_CLASSPATH, this.searchClassPath);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getEnumeratorModules()
     */
    @Override
    public List<EnumeratorModule> getEnumeratorModules()
    {
        this.readDiscoverySettings();

        List<EnumeratorModule> enumeratorModules = new ArrayList<EnumeratorModule>();

        if (this.searchClassPath)
        {
            enumeratorModules.add(new EnumeratorModuleClassPath());
        }

        if (this.searchModsFolder)
        {
            File modsFolder = this.environment.getModsFolder();
            enumeratorModules.add(new EnumeratorModuleFolder(this, modsFolder, false));

            File versionedModsFolder = this.environment.getVersionedModsFolder();
            enumeratorModules.add(new EnumeratorModuleFolder(this, versionedModsFolder, true));
        }

        return Collections.unmodifiableList(enumeratorModules);
    }

    /**
     * Get the ObjectFactory
     */
    public abstract ObjectFactory<?, ?> getObjectFactory();
    
    @Override
    public MixinConfigProvider getMixins()
    {
        return this;
    }
    
    @Override
    public CompatibilityLevel getCompatibilityLevel()
    {
        return null;
    }
    
    @Override
    public String[] getMixinConfigs()
    {
        return new String[] {
            "mixins.liteloader.core.json"
        };
    }
    
    @Override
    public String[] getErrorHandlers()
    {
        return null;
    }
}
