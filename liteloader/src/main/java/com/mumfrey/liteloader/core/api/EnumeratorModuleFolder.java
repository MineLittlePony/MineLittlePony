package com.mumfrey.liteloader.core.api;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.base.Charsets;
import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.core.LiteLoaderVersion;
import com.mumfrey.liteloader.interfaces.LoadableFile;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.ModularEnumerator;
import com.mumfrey.liteloader.interfaces.TweakContainer;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * Enumerator module which searches for mods and tweaks in a folder
 * 
 * @author Adam Mummery-Smith
 */
public class EnumeratorModuleFolder implements FilenameFilter, EnumeratorModule
{
    /**
     * Ordered sets used to sort mods by version/revision  
     */
    protected final Map<String, TreeSet<LoadableMod<File>>> versionOrderingSets = new HashMap<String, TreeSet<LoadableMod<File>>>();

    /**
     * Mods to add once init is completed
     */
    protected final List<LoadableMod<File>> loadableMods = new ArrayList<LoadableMod<File>>();

    protected LiteLoaderCoreAPI coreAPI;

    protected File directory;

    protected boolean readJarFiles;
    protected boolean loadTweaks;
    protected boolean forceInjection;

    /**
     * True if this is a versioned folder and the enumerator should also try to
     * load tweak jars which would normally be ignored.
     */
    protected final boolean loadTweakJars;

    public EnumeratorModuleFolder(LiteLoaderCoreAPI coreAPI, File directory, boolean loadTweakJars)
    {
        this.coreAPI         = coreAPI;
        this.directory       = directory;
        this.loadTweakJars   = loadTweakJars;
    }

    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.loadTweaks = properties.loadTweaksEnabled();
        this.readJarFiles = properties.getAndStoreBooleanProperty(LoaderProperties.OPTION_SEARCH_JARFILES, true);
        this.forceInjection = properties.getAndStoreBooleanProperty(LoaderProperties.OPTION_FORCE_INJECTION, false);

        this.coreAPI.writeDiscoverySettings();
    }

    /**
     * Write settings
     */
    @Override
    public void writeSettings(LoaderEnvironment environment, LoaderProperties properties)
    {
        properties.setBooleanProperty(LoaderProperties.OPTION_SEARCH_JARFILES, this.readJarFiles);
        properties.setBooleanProperty(LoaderProperties.OPTION_FORCE_INJECTION, this.forceInjection);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.directory.getAbsolutePath();
    }

    /**
     * Get the directory this module will inspect
     */
    public File getDirectory()
    {
        return this.directory;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.Enumerator#getLoadableMods()
     */
    public List<LoadableMod<File>> getLoadableMods()
    {
        return this.loadableMods;
    }

    /**
     * For FilenameFilter interface
     * 
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String fileName)
    {
        fileName = fileName.toLowerCase();

        if (fileName.endsWith(".litemod.zip"))
        {
            LiteLoaderLogger.warning("Found %s with unsupported extension .litemod.zip."
                    + " Please change file extension to .litemod to allow this file to be loaded!", fileName);
            return true;
        }

        return fileName.endsWith(".litemod") || fileName.endsWith(".jar");
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.Enumerator
     *      #enumerate(com.mumfrey.liteloader.core.EnabledModsList,
     *      java.lang.String)
     */
    @Override
    public void enumerate(ModularEnumerator enumerator, String profile)
    {
        if (this.directory.exists() && this.directory.isDirectory())
        {
            LiteLoaderLogger.info("Discovering valid mod files in folder %s", this.directory.getPath());

            this.findValidFiles(enumerator);
            this.sortAndRegisterFiles(enumerator);
        }
    }

    /**
     * Search the folder for (potentially) valid files
     */
    private void findValidFiles(ModularEnumerator enumerator)
    {
        for (File file : this.directory.listFiles(this.getFilenameFilter()))
        {
            LoadableFile candidateFile = new LoadableFile(file);
            candidateFile.setForceInjection(this.forceInjection);
            try
            {
                this.inspectFile(enumerator, candidateFile);
            }
            catch (Exception ex)
            {
                LiteLoaderLogger.warning(ex, "An error occurred whilst inspecting %s", candidateFile);
            }
        }
    }

    /**
     * Check whether a particular file is valid, and add it to the candiates
     * list if it appears to be acceptable.
     * 
     * @param enumerator
     * @param candidateFile
     */
    protected void inspectFile(ModularEnumerator enumerator, LoadableFile candidateFile)
    {
        if (this.isValidFile(enumerator, candidateFile))
        {
            String metaData = candidateFile.getFileContents(LoadableMod.METADATA_FILENAME, Charsets.UTF_8);
            if (metaData != null)
            {
                LoadableMod<File> modFile = this.getModFile(candidateFile, metaData);
                this.addModFile(enumerator, modFile);
                return;
            }
            else if (this.isValidTweakContainer(candidateFile))
            {
                TweakContainer<File> container = this.getTweakFile(candidateFile);
                this.addTweakFile(enumerator, container);
                return;
            }
            else
            {
                LiteLoaderLogger.info("Ignoring %s", candidateFile);
//                enumerator.registerBadContainer(candidateFile, "No metadata");
            }
        }
//        else
//        {
//            enumerator.registerBadContainer(candidateFile, "Not a valid file");
//        }
    }

    /**
     * Check whether the specified file is a valid mod container
     * 
     * @param enumerator
     * @param candidateFile
     */
    protected boolean isValidFile(ModularEnumerator enumerator, LoadableFile candidateFile)
    {
        String filename = candidateFile.getName().toLowerCase();
        if (filename.endsWith(".litemod.zip"))
        {
            enumerator.registerBadContainer(candidateFile, "Invalid file extension .litemod.zip");
            return false;
        }
        else if (filename.endsWith(".litemod"))
        {
            return true;
        }
        else if (filename.endsWith(".jar"))
        {
            Set<String> modSystems = candidateFile.getModSystems();
            boolean hasLiteLoader = modSystems.contains("LiteLoader");
            if (modSystems.size() > 0)
            {
                LiteLoaderLogger.info("%s supports mod systems %s", candidateFile, modSystems);
                if (!hasLiteLoader) return false;
            }

            return this.loadTweakJars || this.readJarFiles || hasLiteLoader;
        }

        return false;
    }

    /**
     * Called only if the file is not a valid mod container (has no mod
     * metadata) to check whether it could instead be a potential tweak
     * container.
     * 
     * @param candidateFile
     */
    protected boolean isValidTweakContainer(LoadableFile candidateFile)
    {
        return this.loadTweakJars && this.loadTweaks && candidateFile.getName().toLowerCase().endsWith(".jar");
    }

    /**
     * Get the {@link FilenameFilter} to use to filter candidate files
     */
    protected FilenameFilter getFilenameFilter()
    {
        return this;
    }

    /**
     * @param modFile
     */
    protected boolean isFileSupported(LoadableMod<File> modFile)
    {
        return LiteLoaderVersion.CURRENT.isVersionSupported(modFile.getTargetVersion());
    }

    /**
     * @param candidateFile
     * @param metaData
     */
    protected LoadableMod<File> getModFile(LoadableFile candidateFile, String metaData)
    {
        return new LoadableModFile(candidateFile, metaData);
    }

    /**
     * @param candidateFile
     */
    protected TweakContainer<File> getTweakFile(LoadableFile candidateFile)
    {
        return candidateFile;
    }

    /**
     * @param enumerator 
     * @param modFile
     */
    protected void addModFile(ModularEnumerator enumerator, LoadableMod<File> modFile)
    {
        if (modFile.hasValidMetaData())
        {
            // Only add the mod if the version matches, we add candidates to the versionOrderingSets in
            // order to determine the most recent version available.
            if (this.isFileSupported(modFile))
            {
                if (!this.versionOrderingSets.containsKey(modFile.getName()))
                {
                    this.versionOrderingSets.put(modFile.getModName(), new TreeSet<LoadableMod<File>>());
                }

                LiteLoaderLogger.info("Considering valid mod file: %s", modFile);
                this.versionOrderingSets.get(modFile.getModName()).add(modFile);
            }
            else
            {
                LiteLoaderLogger.info(Verbosity.REDUCED, "Not adding invalid or version-mismatched mod file: %s", modFile);
                enumerator.registerBadContainer(modFile, "Version not supported");
            }
        }
    }

    /**
     * @param enumerator
     * @param container
     */
    protected void addTweakFile(ModularEnumerator enumerator, TweakContainer<File> container)
    {
        enumerator.registerTweakContainer(container);
    }

    /**
     * @param enumerator 
     */
    protected void sortAndRegisterFiles(ModularEnumerator enumerator)
    {
        // Copy the first entry in every version set into the modfiles list
        for (Entry<String, TreeSet<LoadableMod<File>>> modFileEntry : this.versionOrderingSets.entrySet())
        {
            LoadableMod<File> newestVersion = modFileEntry.getValue().iterator().next();
            this.registerFile(enumerator, newestVersion);
        }

        this.versionOrderingSets.clear();
    }

    /**
     * @param enumerator
     * @param modFile
     */
    @SuppressWarnings("unchecked")
    protected void registerFile(ModularEnumerator enumerator, LoadableMod<File> modFile)
    {
        if (enumerator.registerModContainer(modFile))
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Adding newest valid mod file '%s' at revision %.4f", modFile, modFile.getRevision());
            this.loadableMods.add(modFile);
        }
        else
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Not adding valid mod file '%s', the specified mod is disabled or missing a required dependency",
                    modFile);
        }

        if (this.loadTweaks)
        {
            try
            {
                if (modFile instanceof TweakContainer)
                {
                    this.addTweakFile(enumerator, (TweakContainer<File>)modFile);
                }
            }
            catch (Throwable th)
            {
                LiteLoaderLogger.warning("Error adding tweaks from '%s'", modFile);
            }
        }
    }

    @Override
    public void injectIntoClassLoader(ModularEnumerator enumerator, LaunchClassLoader classLoader)
    {
        LiteLoaderLogger.info("Injecting external mods into class path...");

        for (LoadableMod<?> loadableMod : this.loadableMods)
        {
            try
            {
                if (loadableMod.injectIntoClassPath(classLoader, false))
                {
                    LiteLoaderLogger.info("Successfully injected mod file '%s' into classpath", loadableMod);
                }
            }
            catch (MalformedURLException ex)
            {
                LiteLoaderLogger.warning("Error injecting '%s' into classPath. The mod will not be loaded", loadableMod);
            }
        }
    }

    @Override
    public void registerMods(ModularEnumerator enumerator, LaunchClassLoader classLoader)
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "Discovering mods in valid mod files...");
        LoadingProgress.incTotalLiteLoaderProgress(this.loadableMods.size());

        for (LoadableMod<?> modFile : this.loadableMods)
        {
            LoadingProgress.incLiteLoaderProgress("Searching for mods in " + modFile.getModName() + "...");
            LiteLoaderLogger.info("Searching %s...", modFile);
            try
            {
                enumerator.registerModsFrom(modFile, true);
            }
            catch (Exception ex)
            {
                LiteLoaderLogger.warning("Error encountered whilst searching in %s...", modFile);
            }
        }
    }
}
