package com.mumfrey.liteloader.core.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.LaunchClassLoader;

import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.ModularEnumerator;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * Enumerator module which searches for mods on the classpath
 * 
 * @author Adam Mummery-Smith
 */
public class EnumeratorModuleClassPath implements EnumeratorModule
{
    /**
     * Array of class path entries specified to the JVM instance 
     */
    private final String[] classPathEntries;

    /**
     * URLs to add once init is completed
     */
    private final List<LoadableMod<File>> loadableMods = new ArrayList<LoadableMod<File>>();

    private boolean loadTweaks;

    public EnumeratorModuleClassPath()
    {
        // Read the JVM class path into the local array
        this.classPathEntries = this.readClassPath();
    }

    @Override
    public String toString()
    {
        return "<Java Class Path>";
    }

    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.loadTweaks = properties.loadTweaksEnabled();
    }

    @Override
    public void writeSettings(LoaderEnvironment environment, LoaderProperties properties)
    {
    }

    /**
     * Reads the class path entries that were supplied to the JVM and returns
     * them as an array.
     */
    private String[] readClassPath()
    {
        LiteLoaderLogger.info("Enumerating class path...");

        String classPath = System.getProperty("java.class.path");
        String classPathSeparator = System.getProperty("path.separator");
        String[] classPathEntries = classPath.split(classPathSeparator);

        LiteLoaderLogger.info("Class path separator=\"%s\"", classPathSeparator);
        LiteLoaderLogger.info("Class path entries=(\n   classpathEntry=%s\n)", classPath.replace(classPathSeparator, "\n   classpathEntry="));
        return classPathEntries;
    }

    @Override
    public void enumerate(ModularEnumerator enumerator, String profile)
    {
        if (this.loadTweaks)
        {
            LiteLoaderLogger.info("Discovering tweaks on class path...");

            for (String classPathPart : this.classPathEntries)
            {
                try
                {
                    File packagePath = new File(classPathPart);
                    if (packagePath.exists())
                    {
                        LoadableModClassPath classPathMod = new LoadableModClassPath(packagePath);
                        if (enumerator.registerModContainer(classPathMod))
                        {
                            this.loadableMods.add(classPathMod);
                            if (classPathMod.requiresPreInitInjection())
                            {
                                enumerator.registerTweakContainer(classPathMod);
                            }
                        }
                        else
                        {
                            LiteLoaderLogger.info(Verbosity.REDUCED, "Mod %s is disabled or missing a required dependency, not injecting tranformers",
                                    classPathMod.getIdentifier());
                        }
                    }
                }
                catch (Throwable th)
                {
                    LiteLoaderLogger.warning(th, "Error encountered whilst inspecting %s", classPathPart);
                }
            }
        }
    }

    @Override
    public void injectIntoClassLoader(ModularEnumerator enumerator, LaunchClassLoader classLoader)
    {
    }

    /**
     * @param classLoader
     */
    @Override
    public void registerMods(ModularEnumerator enumerator, LaunchClassLoader classLoader)
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "Discovering mods on class path...");
        LoadingProgress.incTotalLiteLoaderProgress(this.loadableMods.size());

        for (LoadableMod<File> classPathMod : this.loadableMods)
        {
            LiteLoaderLogger.info("Searching %s...", classPathMod);
            LoadingProgress.incLiteLoaderProgress("Searching for mods in " + classPathMod.getModName() + "...");
            try
            {
                enumerator.registerModsFrom(classPathMod, true);
            }
            catch (Exception ex)
            {
                LiteLoaderLogger.warning(ex, "Error encountered whilst searching in %s...", classPathMod);
            }
        }
    }
}
