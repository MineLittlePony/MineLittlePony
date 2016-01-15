package com.mumfrey.liteloader.core.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import joptsimple.internal.Strings;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mumfrey.liteloader.api.manager.APIProvider;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.interfaces.LoadableFile;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.launch.InjectionStrategy;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Wrapper for file which represents a mod file to load with associated version
 * information and metadata. Retrieve this from litemod.json at enumeration
 * time. We also override comparable to provide our own custom sorting logic
 * based on version info.
 *
 * @author Adam Mummery-Smith
 */
public class LoadableModFile extends LoadableFile implements LoadableMod<File>
{
    private static final long serialVersionUID = -7952147161905688459L;

    /**
     * Maximum recursion depth for mod discovery
     */
    private static final int MAX_DISCOVERY_DEPTH = 16;

    /**
     * Gson parser for JSON
     */
    protected static Gson gson = new Gson();

    /**
     * True if the metadata information is parsed successfully, the mod will be
     * added.
     */
    protected boolean valid = false;

    /**
     * Name of the mod specified in the JSON file, this can be any string but
     * should be the same between mod versions.
     */
    protected String modName;

    /**
     * Loader version
     */
    protected String targetVersion;

    /**
     * Name of the class transof
     */
    protected List<String> classTransformerClassNames = new ArrayList<String>();

    /**
     * File time stamp, used as sorting criteria when no revision information is
     * found.
     */
    protected long timeStamp;

    /**
     * Revision number from the json file
     */
    protected float revision = 0.0F;

    /**
     * True if the revision number was successfully read, used as a semaphore so
     * that we know when revision is a valid number.
     */
    protected boolean hasRevision = false;

    /**
     * ALL of the parsed metadata from the file, associated with the mod later
     * on for retrieval via the loader.
     */
    protected Map<String, Object> metaData = new HashMap<String, Object>();

    /**
     * Dependencies declared in the metadata
     */
    private Set<String> dependencies = new HashSet<String>();

    /**
     * Dependencies which are missing 
     */
    private Set<String> missingDependencies = new HashSet<String>();;

    /**
     * Required APIs declared in the metadata
     */
    private Set<String> requiredAPIs = new HashSet<String>();

    /**
     * Required APIs which are missing 
     */
    private Set<String> missingAPIs = new HashSet<String>();

    /**
     * Classes in this container 
     */
    protected List<String> classNames = null;

    /**
     * @param file
     * @param metaData
     */
    protected LoadableModFile(File file, String metaData)
    {
        super(file.getAbsolutePath());
        this.init(metaData);
    }

    /**
     * @param file
     * @param metaData
     */
    protected LoadableModFile(LoadableFile file, String metaData)
    {
        super(file);
        this.init(metaData);
    }

    /**
     * @param metaData
     */
    @SuppressWarnings("unchecked")
    protected void init(String metaData)
    {
        this.timeStamp = this.lastModified();
        this.tweakPriority = 0;

        if (!Strings.isNullOrEmpty(metaData))
        {
            try
            {
                this.metaData = LoadableModFile.gson.fromJson(metaData, HashMap.class);
            }
            catch (JsonSyntaxException jsx)
            {
                LiteLoaderLogger.warning("Error reading %s in %s, JSON syntax exception: %s",
                        LoadableMod.METADATA_FILENAME, this.getAbsolutePath(), jsx.getMessage());
                return;
            }

            this.valid = this.parseMetaData();
        }
    }

    protected boolean parseMetaData()
    {
        try
        {
            this.modName     = this.getMetaValue("name", this.getDefaultName());
            this.displayName = this.getMetaValue("displayName", this.modName);
            this.version     = this.getMetaValue("version", "Unknown");
            this.author      = this.getMetaValue("author", "Unknown");

            if (!this.parseVersions()) return false;

            this.injectionStrategy = InjectionStrategy.parseStrategy(this.getMetaValue("injectAt", null));

            this.tweakClassName = this.getMetaValue("tweakClass", this.tweakClassName);

            this.getMetaValuesInto(this.classTransformerClassNames, "classTransformerClasses", ",");
            this.getMetaValuesInto(this.dependencies, "dependsOn", ",");
            this.getMetaValuesInto(this.requiredAPIs, "requiredAPIs", ",");
            this.getMetaValuesInto(this.mixinConfigs, "mixinConfigs", ",");
        }
        catch (ClassCastException ex)
        {
            LiteLoaderLogger.debug(ex);
            LiteLoaderLogger.warning("Error parsing version metadata file in %s, check the format of the file", this.getAbsolutePath());
        }

        return true;
    }

    public boolean parseVersions()
    {
        this.targetVersion = this.getMetaValue("mcversion", null);
        if (this.targetVersion == null)
        {
            LiteLoaderLogger.warning("Mod in %s has no loader version number reading %s", this.getAbsolutePath(), LoadableMod.METADATA_FILENAME);
            return false;
        }

        try
        {
            this.revision = Float.parseFloat(this.getMetaValue("revision", null));
            this.hasRevision = true;
        }
        catch (NullPointerException ex) {}
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Mod in %s has an invalid revision number reading %s", this.getAbsolutePath(), LoadableMod.METADATA_FILENAME);
        }

        return true;
    }

    protected String getDefaultName()
    {
        return this.getName().replaceAll("[^a-zA-Z]", "");
    }

    @Override
    public String getModName()
    {
        return this.modName;
    }

    @Override
    public String getIdentifier()
    {
        return this.modName.toLowerCase();
    }

    @Override
    public String getDescription(String key)
    {
        if (this.missingAPIs.size() > 0)
        {
            return LiteLoader.translate("gui.description.missingapis", "\n" + this.compileMissingAPIList());
        }

        if (this.missingDependencies.size() > 0)
        {
            return LiteLoader.translate("gui.description.missingdeps", "\n" + this.missingDependencies.toString());
        }

        String descriptionKey = "description";
        if (key != null && key.length() > 0)
        {
            descriptionKey += "." + key.toLowerCase();
        }

        return this.getMetaValue(descriptionKey, this.getMetaValue("description", ""));
    }

    private String compileMissingAPIList()
    {
        StringBuilder missingAPIList = new StringBuilder();

        for (String missingAPI : this.missingAPIs)
        {
            if (missingAPI != null)
            {
                if (missingAPI.contains("@"))
                {
                    Matcher matcher = APIProvider.idAndRevisionPattern.matcher(missingAPI);
                    if (matcher.matches())
                    {
                        missingAPIList.append("   ").append(matcher.group(1)).append(" (revision ").append(matcher.group(2)).append(")\n");
                        continue;
                    }
                }

                missingAPIList.append("   ").append(missingAPI).append("\n");
            }
        }

        return missingAPIList.toString();
    }

    @Override
    public boolean isEnabled(LoaderEnvironment environment)
    {
        return this.missingDependencies.size() == 0 && this.missingAPIs.size() == 0 && super.isEnabled(environment);
    }

    @Override
    public boolean isExternalJar()
    {
        return false;
    }

    @Override
    public boolean isToggleable()
    {
        return true;
    }

    @Override
    public boolean hasValidMetaData()
    {
        return this.valid;
    }

    @Override
    public String getTargetVersion()
    {
        return this.targetVersion;
    }

    @Override
    public float getRevision()
    {
        return this.revision;
    }

    protected Object getMetaValue(String metaKey)
    {
        Object metaValue = this.metaData.get(metaKey);
        if (metaValue != null) return metaValue;
        return this.metaData.get(metaKey.toLowerCase());
    }

    @Override
    public String getMetaValue(String metaKey, String defaultValue)
    {
        Object metaValue = this.getMetaValue(metaKey);
        return metaValue != null ? metaValue.toString() : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public String[] getMetaValues(String metaKey, String separator)
    {
        Object metaValue = this.getMetaValue(metaKey);

        if (metaValue instanceof String)
        {
            return ((String)metaValue).split(separator);
        }
        else if (metaValue instanceof ArrayList)
        {
            return ((ArrayList<String>)metaValue).toArray(new String[0]);
        }

        return new String[0];
    }

    protected void getMetaValuesInto(Collection<String> collection, String metaKey, String separator)
    {
        for (String name : this.getMetaValues(metaKey, separator))
        {
            if (!Strings.isNullOrEmpty(name))
            {
                collection.add(name);
            }
        }
    }

    @Override
    public Set<String> getMetaDataKeys()
    {
        return Collections.unmodifiableSet(this.metaData.keySet());
    }

    @Override
    public boolean hasClassTransformers()
    {
        return this.classTransformerClassNames.size() > 0;
    }

    @Override
    public List<String> getClassTransformerClassNames()
    {
        return this.classTransformerClassNames;
    }

    @Override
    public boolean hasResources()
    {
        return true;
    }

    @Override
    public boolean hasDependencies()
    {
        return this.dependencies.size() > 0;
    }

    @Override
    public Set<String> getDependencies()
    {
        return this.dependencies;
    }

    @Override
    public void registerMissingDependency(String dependency)
    {
        this.missingDependencies.add(dependency);
    }

    @Override
    public Set<String> getMissingDependencies()
    {
        return this.missingDependencies;
    }

    @Override
    public Set<String> getRequiredAPIs()
    {
        return this.requiredAPIs;
    }

    @Override
    public void registerMissingAPI(String identifier)
    {
        this.missingAPIs.add(identifier);
    }

    @Override
    public Set<String> getMissingAPIs()
    {
        return this.missingAPIs;
    }

    @Override
    public List<String> getContainedClassNames()
    {
        if (this.classNames == null)
        {
            this.classNames = this.enumerateClassNames();
        }

        return this.classNames;
    }

    protected List<String> enumerateClassNames()
    {
        if (this.isDirectory())
        {
            return LoadableModFile.enumerateDirectory(new ArrayList<String>(), this, "", 0);
        }

        return LoadableModFile.enumerateZipFile(this);
    }

    @Override
    public void addContainedMod(String modName)
    {
    }

    @Override
    public int compareTo(File other)
    {
        if (other == null || !(other instanceof LoadableModFile)) return -1;

        LoadableModFile otherMod = (LoadableModFile)other;

        // If the other object has a revision, compare revisions
        if (otherMod.hasRevision)
        {
            return this.hasRevision && this.revision - otherMod.revision > 0 ? -1 : 1;
        }

        // If we have a revision and the other object doesn't, then we are higher
        if (this.hasRevision)
        {
            return -1;
        }

        // Give up and use timestamp
        return (int)(otherMod.timeStamp - this.timeStamp);
    }

    protected static List<String> enumerateZipFile(File file)
    {
        List<String> classes = new ArrayList<String>();

        ZipFile zipFile;
        try
        {
            zipFile = new ZipFile(file);
        }
        catch (IOException ex)
        {
            return classes;
        }

        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zipFile.entries();
        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entry.getSize() > 0 && entryName.endsWith(".class"))
            {
                classes.add(entryName.substring(0, entryName.length() - 6).replace('/', '.'));
            }
        }

        try
        {
            zipFile.close();
        }
        catch (IOException ex) {}

        return classes;
    }

    /**
     * Recursive function to enumerate classes inside a classpath folder
     * 
     * @param classes
     * @param packagePath
     * @param packageName
     */
    protected static List<String> enumerateDirectory(List<String> classes, File packagePath, String packageName, int depth)
    {
        // Prevent crash due to broken recursion
        if (depth > MAX_DISCOVERY_DEPTH)
        {
            return classes;
        }

        File[] classFiles = packagePath.listFiles();

        for (File classFile : classFiles)
        {
            if (classFile.isDirectory())
            {
                LoadableModFile.enumerateDirectory(classes, classFile, packageName + classFile.getName() + ".", depth + 1);
            }
            else
            {
                if (classFile.getName().endsWith(".class"))
                {
                    String classFileName = classFile.getName();
                    classes.add(packageName + classFileName.substring(0, classFileName.length() - 6));
                }
            }
        }

        return classes;
    }

    /**
     * @param zip
     * @param entry
     * @throws IOException
     */
    public static String zipEntryToString(ZipFile zip, ZipEntry entry) throws IOException
    {
        InputStream stream = null; 
        Charset charset = Charsets.UTF_8;
        int bomOffset = 0;
        byte[] bytes;

        try
        {
            stream = zip.getInputStream(entry);
            bytes = ByteStreams.toByteArray(stream);
        }
        finally
        {
            if (stream != null) stream.close();
        }

        if (bytes == null || bytes.length == 0) return "";

        // Handle unicode by looking for BOM
        if (bytes.length > 1)
        {
            if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFE)
            {
                charset = Charsets.UTF_16LE;
                bomOffset = 2;
            }
            else if (bytes[0] == (byte)0xFE && bytes[1] == (byte)0xFF)
            {
                charset = Charsets.UTF_16BE;
                bomOffset = 2;
            }
        }

        return new String(bytes, bomOffset, bytes.length - bomOffset, charset);
    }

    protected static String getVersionMetaDataString(File file)
    {
        return LoadableFile.getFileContents(file, LoadableMod.METADATA_FILENAME, Charsets.UTF_8);
    }
}
