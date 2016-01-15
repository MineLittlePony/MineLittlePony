package com.mumfrey.liteloader.interfaces;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import com.mumfrey.liteloader.core.api.LoadableModFile;
import com.mumfrey.liteloader.launch.ClassPathUtilities;
import com.mumfrey.liteloader.launch.InjectionStrategy;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.launchwrapper.LaunchClassLoader;

public class LoadableFile extends File implements TweakContainer<File>
{
    public static final String MFATT_MODTYPE = "ModType";
    public static final String MFATT_TWEAK_CLASS = "TweakClass";
    public static final String MFATT_CLASS_PATH = "Class-Path";
    public static final String MFATT_TWEAK_ORDER = "TweakOrder";
    public static final String MFATT_IMPLEMENTATION_TITLE = "Implementation-Title";
    public static final String MFATT_TWEAK_NAME = "TweakName";
    public static final String MFATT_IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String MFATT_TWEAK_VERSION = "TweakVersion";
    public static final String MFATT_IMPLEMENTATION_VENDOR = "Implementation-Vendor";
    public static final String MFATT_TWEAK_AUTHOR = "TweakAuthor";
    public static final String MFATT_MIXIN_CONFIGS = "MixinConfigs";
    public static final String MFATT_INJECTION_STRATEGY = "TweakInjectionStrategy";

    private static final Pattern versionPattern = Pattern.compile("([0-9]+\\.)+[0-9]+([_A-Z0-9]+)?");

    private static final long serialVersionUID = 1L;

    /**
     * True once this file has been injected into the class path 
     */
    protected boolean injected;

    protected boolean forceInjection;

    /**
     * Position to inject the mod file at in the class path, if blank injects at
     * the bottom as usual, alternatively the developer can specify "top" to
     * inject at the top, "base" to inject above the game jar, or "above: name"
     * to inject above a specified other library matching "name".
     */
    protected InjectionStrategy injectionStrategy = null;

    protected Set<String> modSystems = new HashSet<String>();

    /**
     * Name of the tweak class
     */
    protected String tweakClassName;

    /**
     * Priority for this tweaker 
     */
    protected int tweakPriority = 1000;

    /**
     * Class path entries read from jar metadata
     */
    protected String[] classPathEntries = null;

    protected String displayName;

    protected String version = "Unknown";

    protected String author = "Unknown";

    protected boolean hasEventTransformers;

    /**
     * Mixin config resource names
     */
    protected Set<String> mixinConfigs = new HashSet<String>();

    /**
     * Create a new tweak container wrapping the specified file
     */
    public LoadableFile(File parent)
    {
        super(parent.getAbsolutePath());
        this.displayName = this.getName();
        this.guessVersionFromName();
        this.readJarMetaData();
    }

    /**
     * ctor for subclasses
     */
    protected LoadableFile(LoadableFile file)
    {
        super(file.getAbsolutePath());
        this.displayName = this.getName();
        this.forceInjection = file.forceInjection;
        this.assignJarMetaData(file);
    }

    /**
     * ctor for subclasses
     */
    protected LoadableFile(String pathname)
    {
        super(pathname);
        this.displayName = this.getName();
        this.readJarMetaData();
    }

    private void guessVersionFromName()
    {
        Matcher versionPatternMatcher = LoadableFile.versionPattern.matcher(this.getName());
        while (versionPatternMatcher.find())
        {
            this.version = versionPatternMatcher.group();
        }
    }

    protected void assignJarMetaData(LoadableFile file)
    {
        this.modSystems        = file.modSystems;
        this.tweakClassName    = file.tweakClassName;
        this.classPathEntries  = file.classPathEntries;
        this.tweakPriority     = file.tweakPriority;
        this.displayName       = file.displayName;
        this.version           = file.version;
        this.author            = file.author;
        this.injectionStrategy = file.injectionStrategy;
    }

    /**
     * Search for tweaks in this file
     */
    protected void readJarMetaData()
    {
        JarFile jar = null;

        if (this.isDirectory())
        {
            return;
        }

        try
        {
            jar = new JarFile(this);
            if (jar.getManifest() != null)
            {
                LiteLoaderLogger.info("Inspecting jar metadata in '%s'", this.getName());
                Attributes mfAttributes = jar.getManifest().getMainAttributes();

                String mfAttmodSystemList     = mfAttributes.getValue(LoadableFile.MFATT_MODTYPE);
                String mfAttTweakClass        = mfAttributes.getValue(LoadableFile.MFATT_TWEAK_CLASS);
                String mfAttClassPath         = mfAttributes.getValue(LoadableFile.MFATT_CLASS_PATH);
                String mfAttTweakOrder        = mfAttributes.getValue(LoadableFile.MFATT_TWEAK_ORDER);
                String mfAttDisplayName       = mfAttributes.getValue(LoadableFile.MFATT_IMPLEMENTATION_TITLE);
                String mfAttTweakName         = mfAttributes.getValue(LoadableFile.MFATT_TWEAK_NAME);
                String mfAttVersion           = mfAttributes.getValue(LoadableFile.MFATT_IMPLEMENTATION_VERSION);
                String mfAttTweakVersion      = mfAttributes.getValue(LoadableFile.MFATT_TWEAK_VERSION);
                String mfAttAuthor            = mfAttributes.getValue(LoadableFile.MFATT_IMPLEMENTATION_VENDOR);
                String mfAttTweakAuthor       = mfAttributes.getValue(LoadableFile.MFATT_TWEAK_AUTHOR);
                String mfAttMixinConfigs      = mfAttributes.getValue(LoadableFile.MFATT_MIXIN_CONFIGS);
                String mfAttInjectionStrategy = mfAttributes.getValue(LoadableFile.MFATT_INJECTION_STRATEGY);
                
                if (mfAttmodSystemList != null)
                {
                    for (String modSystem : mfAttmodSystemList.split(","))
                    {
                        modSystem = modSystem.trim();
                        if (modSystem.length() > 0)
                        {
                            this.modSystems.add(modSystem);
                        }
                    }
                }

                this.tweakClassName = mfAttTweakClass;
                if (this.tweakClassName != null && mfAttClassPath != null)
                {
                    this.classPathEntries = mfAttClassPath.split(" ");
                }

                if (mfAttTweakOrder != null)
                {
                    Integer tweakOrder = Ints.tryParse(mfAttTweakOrder);
                    if (tweakOrder != null)
                    {
                        this.tweakPriority = tweakOrder.intValue();
                    }
                }

                if (mfAttDisplayName  != null) this.displayName = mfAttDisplayName;
                if (mfAttTweakName    != null) this.displayName = mfAttTweakName;
                if (mfAttVersion      != null) this.version     = mfAttVersion;
                if (mfAttTweakVersion != null) this.version     = mfAttTweakVersion;
                if (mfAttAuthor       != null) this.author      = mfAttAuthor;
                if (mfAttTweakAuthor  != null) this.author      = mfAttTweakAuthor;
                
                if (mfAttMixinConfigs != null)
                {
                    for (String config : mfAttMixinConfigs.split(","))
                    {
                        this.mixinConfigs.add(config);
                    }
                }

                this.injectionStrategy = InjectionStrategy.parseStrategy(mfAttInjectionStrategy, InjectionStrategy.TOP);
            }
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Could not parse jar metadata in '%s'", this);
        }
        finally
        {
            try
            {
                if (jar != null) jar.close();
            }
            catch (IOException ex) {}
        }
    }

    public Set<String> getModSystems()
    {
        return Collections.unmodifiableSet(this.modSystems);
    }

    @Override
    public File getTarget()
    {
        return this;
    }

    @Override
    public File toFile()
    {
        return this;
    }

    @Override
    public String getLocation()
    {
        return this.getAbsolutePath();
    }

    @Override
    public URL getURL() throws MalformedURLException
    {
        return this.toURI().toURL();
    }

    @Override
    public String getIdentifier()
    {
        return this.getName().toLowerCase();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ITweakContainer#hasTweakClass()
     */
    @Override
    public boolean hasTweakClass()
    {
        return this.tweakClassName != null;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ITweakContainer#getTweakClassName()
     */
    @Override
    public String getTweakClassName()
    {
        return this.tweakClassName;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.TweakContainer#getTweakPriority()
     */
    @Override
    public int getTweakPriority()
    {
        return this.tweakPriority;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ITweakContainer#getClassPathEntries()
     */
    @Override
    public String[] getClassPathEntries()
    {
        return this.classPathEntries;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ITweakContainer#hasClassTransformers()
     */
    @Override
    public boolean hasClassTransformers()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.ITweakContainer
     *      #getClassTransformerClassNames()
     */
    @Override
    public List<String> getClassTransformerClassNames()
    {
        return Collections.<String>emptyList();
    }
    
    @Override
    public boolean hasMixins()
    {
        return this.mixinConfigs.size() > 0;
    }
    
    @Override
    public Set<String> getMixinConfigs()
    {
        return this.mixinConfigs;
    }

    @Override
    public boolean hasEventTransformers()
    {
        return this.hasEventTransformers;
    }

    public void onEventsInjected()
    {
        this.hasEventTransformers = true;
    }

    public boolean isInjectionForced()
    {
        return this.forceInjection;
    }

    public void setForceInjection(boolean forceInjection)
    {
        this.forceInjection = forceInjection;
    }
    
    @Override
    public boolean requiresPreInitInjection()
    {
        return this.hasTweakClass() || this.hasClassTransformers() || this.hasMixins();
    }

    @Override
    public boolean isInjected()
    {
        return this.injected;
    }

    @Override
    public boolean injectIntoClassPath(LaunchClassLoader classLoader, boolean injectIntoParent) throws MalformedURLException
    {
        if (!this.injected)
        {
            this.injected = true;

            boolean isOnClassPath = ClassPathUtilities.isJarOnClassPath(this, classLoader);
            if (!this.forceInjection && isOnClassPath)
            {
                LiteLoaderLogger.info("%s already exists on the classpath, skipping injection", this);
                return false;
            }

            ClassPathUtilities.injectIntoClassPath(classLoader, this.getURL(), this.getInjectionStrategy());

            if (injectIntoParent)
            {
                LiteLoaderTweaker.addURLToParentClassLoader(this.getURL());
            }

            return true;

        }

        return false;
    }

    @Override
    public InjectionStrategy getInjectionStrategy()
    {
        return this.injectionStrategy;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName != null ? this.displayName : this.getName();
    }

    @Override
    public String getVersion()
    {
        return this.version;
    }

    @Override
    public String getAuthor()
    {
        return this.author;
    }

    @Override
    public String getDescription(String key)
    {
        return "";
    }

    @Override
    public boolean isExternalJar()
    {
        return true;
    }

    @Override
    public boolean isToggleable()
    {
        return false;
    }

    @Override
    public boolean isEnabled(LoaderEnvironment environment)
    {
        return environment.getEnabledModsList().isEnabled(environment.getProfile(), this.getIdentifier());
    }

    @Override
    public String toString()
    {
        return this.getLocation();
    }

    /**
     * @param name
     * @param charset
     */
    public String getFileContents(String name, Charset charset)
    {
        return LoadableFile.getFileContents(this, name, charset);
    }

    /**
     * @param parent
     * @param name
     * @param charset
     */
    public static String getFileContents(File parent, String name, Charset charset)
    {
        try
        {
            if (parent.isDirectory())
            {
                File file = new File(parent, name);
                if (file.isFile())
                {
                    return Files.toString(file, charset);
                }
            }
            else
            {
                String content = null;
                ZipFile zipFile = new ZipFile(parent);
                ZipEntry zipEntry = zipFile.getEntry(name);
                if (zipEntry != null)
                {
                    try
                    {
                        content = LoadableModFile.zipEntryToString(zipFile, zipEntry);
                    }
                    catch (IOException ex) {}
                }

                zipFile.close();
                return content;
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}