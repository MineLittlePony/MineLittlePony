package com.mumfrey.liteloader.core.api;

import java.io.File;
import java.net.MalformedURLException;

import net.minecraft.launchwrapper.LaunchClassLoader;

import com.mumfrey.liteloader.core.LiteLoaderVersion;

/**
 * Mod file reference for a file loaded from class path
 *
 * @author Adam Mummery-Smith
 */
public class LoadableModClassPath extends LoadableModFile
{
    private static final long serialVersionUID = -4759310661966590773L;

    private boolean modNameRequired = false;

    LoadableModClassPath(File file)
    {
        this(file, null);
    }

    LoadableModClassPath(File file, String fallbackName)
    {
        super(file, LoadableModFile.getVersionMetaDataString(file));

        if (this.modName == null)
        {
            if (fallbackName != null)
            {
                this.modName = fallbackName;
            }
            else if (this.isFile())
            {
                this.modName = this.getName().substring(0, this.getName().lastIndexOf('.'));
            }
            else
            {
                String parentFileName = this.getParentFile() != null ? this.getParentFile().getName().toLowerCase() : "";
                this.modName = String.format("%s.%s", parentFileName, this.getName().toLowerCase());
                this.modNameRequired = true;
            }
        }

        if (this.targetVersion == null) this.targetVersion = LiteLoaderVersion.CURRENT.getMinecraftVersion();
    }

    @Override
    protected void readJarMetaData()
    {
        // Nope
    }

    @Override
    protected String getDefaultName()
    {
        return null;
    }

    @Override
    public String getDisplayName()
    {
        return this.getModName();
    }

    @Override
    public boolean injectIntoClassPath(LaunchClassLoader classLoader, boolean injectIntoParent) throws MalformedURLException
    {
        // Can't inject a class path entry into the class path!
        return false;
    }

    @Override
    public boolean isInjected()
    {
        return true;
    }

    @Override
    public void addContainedMod(String modName)
    {
        if (this.modNameRequired)
        {
            this.modNameRequired = false;
            this.modName = modName;
        }
    }
}
