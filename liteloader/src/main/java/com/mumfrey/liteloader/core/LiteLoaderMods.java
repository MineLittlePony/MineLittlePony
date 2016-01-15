package com.mumfrey.liteloader.core;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.ModLoadObserver;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.LoaderEnumerator;
import com.mumfrey.liteloader.interfaces.TweakContainer;
import com.mumfrey.liteloader.launch.ClassTransformerManager;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.modconfig.ConfigManager;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * Separated from the core loader class for encapsulation purposes
 *
 * @author Adam Mummery-Smith
 */
public class LiteLoaderMods
{
    public static final String MOD_SYSTEM = "liteloader";

    /**
     * Reference to the loader 
     */
    protected final LiteLoader loader;

    /**
     * Loader environment instance 
     */
    protected final LoaderEnvironment environment;

    /**
     * Loader Properties adapter 
     */
    private final LoaderProperties properties;

    /**
     * Mod enumerator instance
     */
    protected final LoaderEnumerator enumerator;

    /**
     * Configuration manager 
     */
    private final ConfigManager configManager;

    /**
     * Mod load observers
     */
    private FastIterableDeque<ModLoadObserver> observers = new HandlerList<ModLoadObserver>(ModLoadObserver.class);

    /**
     * List of loaded mods, for crash reporting
     */
    private String loadedModsList = "none";

    /**
     * Global list of mods which we can load
     */
    protected final List<Mod> allMods = new LinkedList<Mod>();

    /**
     * Global list of mods which are still waiting for initialisiation
     */
    protected final Deque<Mod> initMods = new LinkedList<Mod>();

    /**
     * Global list of mods which we have loaded
     */
    protected final List<Mod> loadedMods = new LinkedList<Mod>();

    /**
     * Global list of mods which we found but ignored (eg. outdated, invalid)
     */
    protected final List<Mod> badMods = new LinkedList<Mod>();

    /**
     * Mods which are loaded but disabled
     */
    protected final List<ModInfo<?>> disabledMods = new LinkedList<ModInfo<?>>();

    /**
     * Bad containers
     */
    protected final List<ModInfo<?>> badContainers = new LinkedList<ModInfo<?>>();

    private int startupErrorCount, criticalErrorCount;

    LiteLoaderMods(LiteLoader loader, LoaderEnvironment environment, LoaderProperties properties, ConfigManager configManager)
    {
        this.loader           = loader;
        this.environment      = environment;
        this.enumerator       = environment.getEnumerator();
        this.properties       = properties;
        this.configManager    = configManager;
    }

    void init(List<ModLoadObserver> observers)
    {
        this.observers.addAll(observers);
        this.disabledMods.addAll(this.enumerator.getDisabledContainers());
        this.badContainers.addAll(this.enumerator.getBadContainers());
    }

    void onPostInit()
    {
        this.updateSharedModList();

        this.environment.getEnabledModsList().save();
    }

    public EnabledModsList getEnabledModsList()
    {
        return this.environment.getEnabledModsList();
    }

    public List<Mod> getAllMods()
    {
        return Collections.unmodifiableList(this.allMods);
    }

    /**
     * Used for crash reporting, returns a text list of all loaded mods
     * 
     * @return List of loaded mods as a string
     */
    public String getLoadedModsList()
    {
        return this.loadedModsList;
    }

    /**
     * Get a list containing all loaded mods
     */
    public List<? extends ModInfo<LoadableMod<?>>> getLoadedMods()
    {
        return this.loadedMods;
    }

    /**
     * Get a list containing all mod files which were NOT loaded
     */
    public List<? extends ModInfo<?>> getDisabledMods()
    {
        return this.disabledMods;
    }

    /**
     * Get a list of all bad containers
     */
    public List<? extends ModInfo<?>> getBadContainers()
    {
        return this.badContainers;
    }

    /**
     * Get the list of injected tweak containers
     */
    public Collection<? extends ModInfo<Loadable<?>>> getInjectedTweaks()
    {
        return this.enumerator.getInjectedTweaks();
    }

    public int getStartupErrorCount()
    {
        return this.startupErrorCount;
    }

    public int getCriticalErrorCount()
    {
        return this.criticalErrorCount;
    }

    public ModInfo<?> getModInfo(LiteMod instance)
    {
        for (Mod mod : this.allMods)
        {
            if (instance == mod.getMod())
            {
                return mod;
            }
        }

        return null;
    }

    /**
     * Get whether the specified mod is installed
     *
     * @param modName
     */
    public boolean isModInstalled(String modName)
    {
        try
        {
            return this.getMod(modName) != null;
        }
        catch (IllegalArgumentException ex)
        {
            return false;
        }
    }

    /**
     * Get a reference to a loaded mod, if the mod exists
     * 
     * @param modName Mod's name, identifier or class name
     */
    @SuppressWarnings("unchecked")
    public <T extends LiteMod> T getMod(String modName)
    {
        if (modName == null)
        {
            throw new IllegalArgumentException("Attempted to get a reference to a mod without specifying a mod name");
        }

        for (Mod mod : this.allMods)
        {
            if (mod.matchesName(modName))
            {
                return (T)mod.getMod();
            }
        }

        return null;
    }

    /**
     * Get a reference to a loaded mod, if the mod exists
     * 
     * @param modClass Mod class
     */
    @SuppressWarnings("unchecked")
    public <T extends LiteMod> T getMod(Class<T> modClass)
    {
        for (Mod mod : this.allMods)
        {
            if (mod.getModClass().equals(modClass))
            {
                return (T)mod.getMod();
            }
        }

        return null;
    }

    /**
     * Get the mod which matches the specified identifier
     * 
     * @param identifier
     */
    public Class<? extends LiteMod> getModFromIdentifier(String identifier)
    {
        if (identifier == null) return null;

        for (Mod mod : this.allMods)
        {
            if (mod.matchesIdentifier(identifier))
            {
                return mod.getModClass();
            }
        }

        return null;
    }

    /**
     * Get a metadata value for the specified mod
     * 
     * @param modNameOrId
     * @param metaDataKey
     * @param defaultValue
     */
    public String getModMetaData(String modNameOrId, String metaDataKey, String defaultValue) throws IllegalArgumentException
    {
        return this.getModMetaData(this.getMod(modNameOrId), metaDataKey, defaultValue);
    }

    /**
     * Get a metadata value for the specified mod
     * 
     * @param mod
     * @param metaDataKey
     * @param defaultValue
     */
    public String getModMetaData(LiteMod mod, String metaDataKey, String defaultValue)
    {
        if (mod == null || metaDataKey == null) return defaultValue;
        return this.enumerator.getModMetaData(mod.getClass(), metaDataKey, defaultValue);
    }

    /**
     * Get a metadata value for the specified mod
     * 
     * @param modClass
     * @param metaDataKey
     * @param defaultValue
     */
    public String getModMetaData(Class<? extends LiteMod> modClass, String metaDataKey, String defaultValue)
    {
        if (modClass == null || metaDataKey == null) return defaultValue;
        return this.enumerator.getModMetaData(modClass, metaDataKey, defaultValue);
    }

    /**
     * Get the mod identifier, this is used for versioning, exclusivity, and
     * enablement checks.
     * 
     * @param modClass
     */
    public String getModIdentifier(Class<? extends LiteMod> modClass)
    {
        return this.enumerator.getIdentifier(modClass);
    }

    /**
     * Get the mod identifier, this is used for versioning, exclusivity, and
     * enablement checks.
     * 
     * @param mod
     */
    public String getModIdentifier(LiteMod mod)
    {
        return mod == null ? null : this.enumerator.getIdentifier(mod.getClass());
    }

    /**
     * Get the container (mod file, classpath jar or folder) for the specified
     * mod.
     * 
     * @param modClass
     */
    public LoadableMod<?> getModContainer(Class<? extends LiteMod> modClass)
    {
        return this.enumerator.getContainer(modClass);
    }

    /**
     * Get the container (mod file, classpath jar or folder) for the specified
     * mod.
     * 
     * @param mod
     */
    public LoadableMod<?> getModContainer(LiteMod mod)
    {
        return mod == null ? null : this.enumerator.getContainer(mod.getClass());
    }

    /**
     * @param identifier Identifier of the mod to enable
     */
    public void enableMod(String identifier)
    {
        this.setModEnabled(identifier, true);
    }

    /**
     * @param identifier Identifier of the mod to disable
     */
    public void disableMod(String identifier)
    {
        this.setModEnabled(identifier, false);
    }

    /**
     * @param identifier Identifier of the mod to enable/disable
     * @param enabled
     */
    public void setModEnabled(String identifier, boolean enabled)
    {
        this.environment.getEnabledModsList().setEnabled(this.environment.getProfile(), identifier, enabled);
        this.environment.getEnabledModsList().save();
    }

    /**
     * @param identifier
     */
    public boolean isModEnabled(String identifier)
    {
        return this.environment.getEnabledModsList().isEnabled(LiteLoader.getProfile(), identifier);
    }

    public boolean isModEnabled(String profile, String identifier)
    {
        return this.environment.getEnabledModsList().isEnabled(profile, identifier);
    }

    /**
     * @param identifier
     */
    public boolean isModActive(String identifier)
    {
        if (identifier == null) return false;

        for (Mod mod : this.loadedMods)
        {
            if (mod.matchesIdentifier(identifier))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Create mod instances from the enumerated classes
     */
    void loadMods()
    {
        LoadingProgress.incTotalLiteLoaderProgress(this.enumerator.getModsToLoad().size());

        for (ModInfo<LoadableMod<?>> mod : this.enumerator.getModsToLoad())
        {
            LoadingProgress.incLiteLoaderProgress("Loading mod from %s...", mod.getModClassSimpleName());
            LoadableMod<?> container = mod.getContainer();

            try
            {
                String identifier = mod.getIdentifier();
                if (identifier == null || this.environment.getEnabledModsList().isEnabled(this.environment.getProfile(), identifier))
                {
                    if (!this.enumerator.checkDependencies(container))
                    {
                        this.onModLoadFailed(container, identifier, "the mod was missing a required dependency", null);
                        continue;
                    }

                    if (mod instanceof Mod)
                    {
                        this.loadMod((Mod)mod);
                    }
                    else
                    {
                        this.loadMod(identifier, mod.getModClass(), container);
                    }
                }
                else
                {
                    this.onModLoadFailed(container, identifier, "excluded by filter", null);
                }
            }
            catch (Throwable th)
            {
                this.onModLoadFailed(container, mod.getModClassName(), "an error occurred", th);
                this.registerModStartupError(mod, th);
            }

            this.observers.all().onPostModLoaded(mod);
        }
    }

    /**
     * @param identifier
     * @param modClass
     * @param container 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    void loadMod(String identifier, Class<? extends LiteMod> modClass, LoadableMod<?> container) throws InstantiationException, IllegalAccessException
    {
        Mod mod = new Mod(container, modClass, identifier);
        this.loadMod(mod);
    }

    /**
     * @param mod
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    void loadMod(Mod mod) throws InstantiationException, IllegalAccessException
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "Loading mod from %s", mod.getModClassName());

        LiteMod newMod = mod.newInstance();

        this.onModLoaded(mod);

        String modName = mod.getDisplayName();
        LiteLoaderLogger.info("Successfully added mod %s version %s", modName, newMod.getVersion());
    }

    /**
     * @param mod
     */
    void onModLoaded(Mod mod)
    {
        this.observers.all().onModLoaded(mod.getMod());

        this.allMods.add(mod);
        this.initMods.add(mod);

        LoadingProgress.incTotalLiteLoaderProgress(1);
    }

    /**
     * @param container
     * @param identifier
     * @param reason
     * @param th
     */
    void onModLoadFailed(LoadableMod<?> container, String identifier, String reason, Throwable th)
    {
        LiteLoaderLogger.warning("Not loading mod %s, %s", identifier, reason);

        for (ModInfo<?> mod : this.disabledMods)
        {
            if (mod.getContainer().equals(container))
            {
                return;
            }
        }

        if (container != LoadableMod.NONE)
        {
            this.disabledMods.add(new NonMod(container, false));
        }

        this.observers.all().onModLoadFailed(container, identifier, reason, th);
    }

    /**
     * Initialise the mods which were loaded
     */
    void initMods()
    {
        this.loadedModsList = "";
        int loadedModsCount = 0;

        while (this.initMods.size() > 0)
        {
            Mod mod = this.initMods.removeFirst();

            try
            {
                this.initMod(mod);
                loadedModsCount++;
            }
            catch (Throwable th)
            {
                this.registerModStartupError(mod, th);
                LiteLoaderLogger.warning(th, "Error initialising mod '%s'", mod.getDisplayName());
            }
        }

        this.loadedModsList = String.format("%s loaded mod(s)%s", loadedModsCount, this.loadedModsList);
    }

    /**
     * @param mod
     */
    private void initMod(Mod mod)
    {
        LiteMod instance = mod.getMod();

        LiteLoaderLogger.info(Verbosity.REDUCED, "Initialising mod %s version %s", instance.getName(), instance.getVersion());
        LoadingProgress.incLiteLoaderProgress("Initialising mod %s version %s...", instance.getName(), instance.getVersion());

        this.onPreInitMod(instance);

        // initialise the mod
        instance.init(LiteLoader.getCommonConfigFolder());

        this.onPostInitMod(instance);

        this.loadedMods.add(mod);
        this.loadedModsList += String.format("\n          - %s version %s", mod.getDisplayName(), mod.getVersion());
    }

    /**
     * @param instance
     */
    private void onPreInitMod(LiteMod instance)
    {
        this.observers.all().onPreInitMod(instance);

        // register mod config panel if configurable
        this.configManager.registerMod(instance);

        try
        {
            this.handleModVersionUpgrade(instance);
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.warning("Error performing settings upgrade for %s. Settings may not be properly migrated", instance.getName());
        }

        // Init mod config if there is any
        this.configManager.initConfig(instance);
    }

    /**
     * @param instance
     */
    private void onPostInitMod(LiteMod instance)
    {
        this.observers.all().onPostInitMod(instance);

        // add the mod to all relevant listener queues
        LiteLoader.getInterfaceManager().offer(instance);

        this.loader.onPostInitMod(instance);
    }

    /**
     * @param instance
     */
    private void handleModVersionUpgrade(LiteMod instance)
    {
        String modKey = this.getModNameForConfig(instance.getClass(), instance.getName());

        int currentRevision = LiteLoaderVersion.CURRENT.getLoaderRevision();
        int lastKnownRevision = this.properties.getLastKnownModRevision(modKey);

        LiteLoaderVersion lastModVersion = LiteLoaderVersion.getVersionFromRevision(lastKnownRevision);
        if (currentRevision > lastModVersion.getLoaderRevision())
        {
            File newConfigPath = LiteLoader.getConfigFolder();
            File oldConfigPath = this.environment.inflectVersionedConfigPath(lastModVersion);

            LiteLoaderLogger.info("Performing config upgrade for mod %s. Upgrading %s to %s...",
                    instance.getName(), lastModVersion, LiteLoaderVersion.CURRENT);

            this.observers.all().onMigrateModConfig(instance, newConfigPath, oldConfigPath);

            // Migrate versioned config if any is present
            this.configManager.migrateModConfig(instance, newConfigPath, oldConfigPath);

            // Let the mod upgrade
            instance.upgradeSettings(LiteLoaderVersion.CURRENT.getMinecraftVersion(), newConfigPath, oldConfigPath);

            this.properties.storeLastKnownModRevision(modKey);
            LiteLoaderLogger.info("Config upgrade succeeded for mod %s", instance.getName());
        }
        else if (currentRevision < lastKnownRevision && ConfigManager.getConfigStrategy(instance) == ConfigStrategy.Unversioned)
        {
            LiteLoaderLogger.warning("Mod %s has config from unknown loader revision %d. This may cause unexpected behaviour.",
                    instance.getName(), lastKnownRevision);
        }
    }

    /**
     * Used by the version upgrade code, gets a version of the mod name suitable
     * for inclusion in the properties file
     * 
     * @param modName
     */
    String getModNameForConfig(Class<? extends LiteMod> modClass, String modName)
    {
        if (modName == null || modName.isEmpty())
        {
            modName = modClass.getSimpleName().toLowerCase();
        }

        return String.format("version.%s", modName.toLowerCase().replaceAll("[^a-z0-9_\\-\\.]", ""));
    }

    void onStartupComplete()
    {
        this.validateModTransformers();
    }

    /**
     * Check that all specified mod transformers were injected successfully, tag
     * mods with failed transformers as critically errored.
     */
    private void validateModTransformers()
    {
        ClassTransformerManager transformerManager = this.environment.getTransformerManager();
        Set<String> injectedTransformers = transformerManager.getInjectedTransformers();

        for (Mod mod : this.loadedMods)
        {
            if (mod.hasClassTransformers())
            {
                List<String> modTransformers = ((TweakContainer<?>)mod.getContainer()).getClassTransformerClassNames();
                for (String modTransformer : modTransformers)
                {
                    if (!injectedTransformers.contains(modTransformer))
                    {
                        List<Throwable> throwables = transformerManager.getTransformerStartupErrors(modTransformer);
                        if (throwables != null)
                        {
                            for (Throwable th : throwables)
                            {
                                this.registerModStartupError(mod, th, true);
                            }
                        }
                        else
                        {
                            this.registerModStartupError(mod, new RuntimeException("Missing class transformer " + modTransformer), true);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param instance
     * @param th
     */
    public void onLateInitFailed(LiteMod instance, Throwable th)
    {
        ModInfo<?> mod = this.getModInfo(instance);
        if (mod != null)
        {
            this.registerModStartupError(mod, th);
        }
    }

    private void registerModStartupError(ModInfo<?> mod, Throwable th)
    {
        // This is a critical error if a mod has already injected a transformer, since it may have injected
        // callbacks which it is not in a position to handle!
        boolean critical = this.hasModInjectedTransformers(mod);

        this.registerModStartupError(mod, th, critical);
    }

    private boolean hasModInjectedTransformers(ModInfo<?> mod)
    {
        if (!mod.hasClassTransformers()) return false;

        Set<String> injectedTransformers = this.environment.getTransformerManager().getInjectedTransformers();
        List<String> modTransformers = ((TweakContainer<?>)mod.getContainer()).getClassTransformerClassNames();

        for (String modTransformer : modTransformers)
        {
            if (injectedTransformers.contains(modTransformer))
            {
                return true;
            }
        }

        return false;
    }

    private void registerModStartupError(ModInfo<?> mod, Throwable th, boolean critical)
    {
        this.startupErrorCount++;
        if (critical) this.criticalErrorCount++;
        mod.registerStartupError(th);

        if (!this.loadedMods.contains(mod) && !this.disabledMods.contains(mod))
        {
            this.disabledMods.add(mod);
        }
    }

    void updateSharedModList()
    {
        Map<String, Map<String, String>> modList = this.enumerator.getSharedModList();
        if (modList == null) return;

        for (Mod mod : this.allMods)
        {
            String modKey = String.format("%s:%s", LiteLoaderMods.MOD_SYSTEM, mod.getIdentifier());
            modList.put(modKey, this.packModInfoToMap(mod));
        }
    }

    private Map<String, String> packModInfoToMap(Mod mod)
    {
        Map<String, String> modInfo = new HashMap<String, String>();

        modInfo.put("modsystem",   LiteLoaderMods.MOD_SYSTEM);
        modInfo.put("id",          mod.getIdentifier());
        modInfo.put("version",     mod.getVersion());
        modInfo.put("name",        mod.getDisplayName());
        modInfo.put("url",         mod.getURL());
        modInfo.put("authors",     mod.getAuthor());
        modInfo.put("description", mod.getDescription());

        return modInfo;
    }
}
