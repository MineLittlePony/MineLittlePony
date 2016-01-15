package com.mumfrey.liteloader.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.spongepowered.asm.mixin.MixinEnvironment;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.*;
import com.mumfrey.liteloader.api.manager.APIAdapter;
import com.mumfrey.liteloader.api.manager.APIProvider;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.core.api.LiteLoaderCoreAPI;
import com.mumfrey.liteloader.core.event.EventProxy;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.crashreport.CallableLaunchWrapper;
import com.mumfrey.liteloader.crashreport.CallableLiteLoaderBrand;
import com.mumfrey.liteloader.crashreport.CallableLiteLoaderMods;
import com.mumfrey.liteloader.interfaces.*;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderEnvironment.EnvironmentType;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.messaging.MessageBus;
import com.mumfrey.liteloader.modconfig.ConfigManager;
import com.mumfrey.liteloader.modconfig.Exposable;
import com.mumfrey.liteloader.permissions.PermissionsManagerClient;
import com.mumfrey.liteloader.permissions.PermissionsManagerServer;
import com.mumfrey.liteloader.transformers.event.EventTransformer;
import com.mumfrey.liteloader.util.Input;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;

/**
 * LiteLoader is a simple loader which loads and provides useful callbacks to
 * lightweight mods
 * 
 * @author Adam Mummery-Smith
 */
public final class LiteLoader
{
    /**
     * LiteLoader is a singleton, this is the singleton instance
     */
    private static LiteLoader instance;

    /**
     * Tweak system class loader 
     */
    private static LaunchClassLoader classLoader;

    /**
     * Reference to the game engine instance
     */
    private GameEngine<?, ?> engine;

    /**
     * Minecraft Profiler
     */
    private Profiler profiler;

    /**
     * Loader environment instance 
     */
    private final LoaderEnvironment environment;

    /**
     * Loader Properties adapter 
     */
    private final LoaderProperties properties;

    /**
     * Mod enumerator instance
     */
    private final LoaderEnumerator enumerator;

    /**
     * Mods
     */
    protected final LiteLoaderMods mods;

    /**
     * API Provider instance 
     */
    private final APIProvider apiProvider;

    /**
     * API Adapter instance
     */
    private final APIAdapter apiAdapter;

    /**
     * Our core API instance
     */
    private final LiteLoaderCoreAPI api;

    /**
     * Factory which can be used to instance main loader helper objects
     */
    private final ObjectFactory<?, ?> objectFactory;

    /**
     * Core providers
     */
    private final FastIterableDeque<CoreProvider> coreProviders = new HandlerList<CoreProvider>(CoreProvider.class);
    private final FastIterableDeque<TickObserver> tickObservers = new HandlerList<TickObserver>(TickObserver.class);
    private final FastIterableDeque<WorldObserver> worldObservers = new HandlerList<WorldObserver>(WorldObserver.class);
    private final FastIterableDeque<ShutdownObserver> shutdownObservers = new HandlerList<ShutdownObserver>(ShutdownObserver.class);
    private final FastIterableDeque<PostRenderObserver> postRenderObservers = new HandlerList<PostRenderObserver>(PostRenderObserver.class);

    /**
     * Mod panel manager, deliberately raw
     */
    @SuppressWarnings("rawtypes")
    private PanelManager panelManager;

    /**
     * Interface Manager
     */
    private LiteLoaderInterfaceManager interfaceManager;

    /**
     * Event manager
     */
    private LiteLoaderEventBroker<?, ?> events;

    /**
     * Plugin channel manager 
     */
    private final ClientPluginChannels clientPluginChannels;

    /**
     * Server channel manager 
     */
    private final ServerPluginChannels serverPluginChannels;

    /**
     * Permission Manager
     */
    private final PermissionsManagerClient permissionsManagerClient;

    private final PermissionsManagerServer permissionsManagerServer;

    /**
     * Mod configuration manager
     */
    private final ConfigManager configManager;

    /**
     * Flag which keeps track of whether late initialisation has completed
     */
    private boolean modInitComplete;

    /**
     * 
     */
    private Input input;

    /**
     * 
     */
    private final List<TranslationProvider> translators = new ArrayList<TranslationProvider>();

    /**
     * ctor
     * 
     * @param environment
     * @param properties
     */
    private LiteLoader(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.environment = environment;
        this.properties = properties;
        this.enumerator = environment.getEnumerator();

        this.configManager = new ConfigManager();

        this.mods = new LiteLoaderMods(this, environment, properties, this.configManager);

        this.apiProvider = environment.getAPIProvider();
        this.apiAdapter = environment.getAPIAdapter();

        this.api = this.apiProvider.getAPI(LiteLoaderCoreAPI.class);
        if (this.api == null)
        {
            throw new IllegalStateException("The core API was not registered. Startup halted");
        }

        this.objectFactory = this.api.getObjectFactory();

        this.input = this.objectFactory.getInput();

        this.clientPluginChannels = this.objectFactory.getClientPluginChannels();
        this.serverPluginChannels = this.objectFactory.getServerPluginChannels();

        this.permissionsManagerClient = this.objectFactory.getClientPermissionManager();
        this.permissionsManagerServer = this.objectFactory.getServerPermissionManager();

        this.initTranslators();
    }

    /**
     * 
     */
    protected void initTranslators()
    {
        for (LiteAPI api : this.apiProvider.getAPIs())
        {
            List<CustomisationProvider> customisationProviders = api.getCustomisationProviders();
            if (customisationProviders != null)
            {
                for (CustomisationProvider provider : customisationProviders)
                {
                    if (provider instanceof TranslationProvider)
                    {
                        this.translators.add((TranslationProvider)provider);
                    }
                }
            }
        }
    }

    /**
     * Set up reflection methods required by the loader
     */
    private void onInit()
    {
        try
        {
            this.coreProviders.addAll(this.apiAdapter.getCoreProviders());
            this.tickObservers.addAll(this.apiAdapter.getAllObservers(TickObserver.class));
            this.worldObservers.addAll(this.apiAdapter.getAllObservers(WorldObserver.class));
            this.shutdownObservers.addAll(this.apiAdapter.getAllObservers(ShutdownObserver.class));
            this.postRenderObservers.addAll(this.apiAdapter.getAllObservers(PostRenderObserver.class));

            this.coreProviders.all().onInit();

            this.enumerator.onInit();
            this.mods.init(this.apiAdapter.getAllObservers(ModLoadObserver.class));
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error initialising LiteLoader", th);
        }
    }

    /**
     * 
     */
    private void onPostInit()
    {
        LoadingProgress.setMessage("LiteLoader POSTINIT...");

        this.initLifetimeObjects();

        this.postInitCoreProviders();

        // Spawn mod instances and initialise them
        this.loadAndInitMods();

        this.coreProviders.all().onPostInitComplete(this.mods);

        // Save stuff
        this.properties.writeProperties();
    }

    /**
     * Get the singleton instance of LiteLoader, initialises the loader if
     * necessary.
     * 
     * @return LiteLoader instance
     */
    public static final LiteLoader getInstance()
    {
        return LiteLoader.instance;
    }

    /**
     * Get the tweak system classloader
     */
    public static LaunchClassLoader getClassLoader()
    {
        return LiteLoader.classLoader;
    }

    /**
     * Get LiteLoader version
     */
    public static final String getVersion()
    {
        return LiteLoaderVersion.CURRENT.getLoaderVersion();
    }

    /**
     * Get LiteLoader version
     */
    public static final String getVersionDisplayString()
    {
        return String.format("LiteLoader %s", LiteLoaderVersion.CURRENT.getLoaderVersion());
    }

    /**
     * Get the loader revision
     */
    public static final int getRevision()
    {
        return LiteLoaderVersion.CURRENT.getLoaderRevision();
    }

    /**
     * Get all active API instances
     */
    public static final LiteAPI[] getAPIs()
    {
        LiteAPI[] apis = LiteLoader.instance.apiProvider.getAPIs();
        LiteAPI[] apisCopy = new LiteAPI[apis.length];
        System.arraycopy(apis, 0, apisCopy, 0, apis.length);
        return apisCopy;
    }

    /**
     * Get an API instance by identifier (returns null if no instance matching
     * the supplied identifier exists).
     * 
     * @param identifier
     */
    public static final LiteAPI getAPI(String identifier)
    {
        return LiteLoader.instance.apiProvider.getAPI(identifier);
    }

    /**
     * @param identifier
     */
    public static boolean isAPIAvailable(String identifier)
    {
        return LiteLoader.getAPI(identifier) != null;
    }

    @SuppressWarnings("unchecked")
    public static final <C extends CustomisationProvider> C getCustomisationProvider(LiteAPI api, Class<C> providerType)
    {
        List<CustomisationProvider> customisationProviders = api.getCustomisationProviders();
        if (customisationProviders != null)
        {
            for (CustomisationProvider provider : customisationProviders)
            {
                if (providerType.isAssignableFrom(provider.getClass())) return (C)provider;
            }
        }

        return null;
    }

    /**
     * Get the client-side permissions manager
     */
    public static PermissionsManagerClient getClientPermissionsManager()
    {
        return LiteLoader.instance.permissionsManagerClient;
    }

    /**
     * Get the server-side permissions manager
     */
    public static PermissionsManagerServer getServerPermissionsManager()
    {
        return LiteLoader.instance.permissionsManagerServer;
    }

    /**
     * Get the current game engine wrapper
     */
    public static GameEngine<?, ?> getGameEngine()
    {
        return LiteLoader.instance.engine;
    }

    /**
     * Get the interface manager
     */
    public static LiteLoaderInterfaceManager getInterfaceManager()
    {
        return LiteLoader.instance.interfaceManager;
    }

    /**
     * Get the client-side plugin channel manager
     */
    public static ClientPluginChannels getClientPluginChannels()
    {
        return LiteLoader.instance.clientPluginChannels;
    }

    /**
     * Get the server-side plugin channel manager
     */
    public static ServerPluginChannels getServerPluginChannels()
    {
        return LiteLoader.instance.serverPluginChannels;
    }

    /**
     * Get the input manager
     */
    public static Input getInput()
    {
        return LiteLoader.instance.input;
    }

    /**
     * Get the mod panel manager
     */
    @SuppressWarnings({ "cast", "unchecked" })
    public static <T> PanelManager<T> getModPanelManager()
    {
        return (PanelManager<T>)LiteLoader.instance.panelManager;
    }

    /**
     * Get the "mods" folder
     */
    public static File getModsFolder()
    {
        return LiteLoader.instance.environment.getModsFolder();
    }

    /**
     * Get the common (version-independent) config folder
     */
    public static File getCommonConfigFolder()
    {
        return LiteLoader.instance.environment.getCommonConfigFolder();
    }

    /**
     * Get the config folder for this version
     */
    public static File getConfigFolder()
    {
        return LiteLoader.instance.environment.getVersionedConfigFolder();
    }

    /**
     * Get the game directory
     */
    public static File getGameDirectory()
    {
        return LiteLoader.instance.environment.getGameDirectory();
    }

    /**
     * Get the "assets" root directory
     */
    public static File getAssetsDirectory()
    {
        return LiteLoader.instance.environment.getAssetsDirectory();
    }

    /**
     * Get the name of the profile which launched the game
     */
    public static String getProfile()
    {
        return LiteLoader.instance.environment.getProfile();
    }

    /**
     * Get the type of environment (client or dedicated server)
     */
    public static EnvironmentType getEnvironmentType()
    {
        return LiteLoader.instance.environment.getType();
    }

    /**
     * Used to get the name of the modpack being used
     * 
     * @return name of the modpack in use or null if no pack
     */
    public static String getBranding()
    {
        return LiteLoader.instance.properties.getBranding();
    }

    /**
     * Get whether the current environment is MCP
     */
    public static boolean isDevelopmentEnvironment()
    {
        return "true".equals(System.getProperty("mcpenv"));
    }

    /**
     * Dump debugging information to the console
     */
    public static void dumpDebugInfo()
    {
        if (LiteLoaderLogger.DEBUG)
        {
            EventTransformer.dumpInjectionState();
            MixinEnvironment.getCurrentEnvironment().audit();
            LiteLoaderLogger.info("Debug info dumped to console");
        }
        else
        {
            LiteLoaderLogger.info("Debug dump not available, developer flag not enabled");
        }
    }

    /**
     * Used for crash reporting, returns a text list of all loaded mods
     * 
     * @return List of loaded mods as a string
     */
    public String getLoadedModsList()
    {
        return this.mods.getLoadedModsList();
    }

    /**
     * Get a list containing all loaded mods
     */
    public List<LiteMod> getLoadedMods()
    {
        List<LiteMod> loadedMods = new ArrayList<LiteMod>();

        for (ModInfo<LoadableMod<?>> loadedMod : this.mods.getLoadedMods())
        {
            loadedMods.add(loadedMod.getMod());
        }

        return loadedMods;
    }

    /**
     * Get a list containing all mod files which were NOT loaded
     */
    public List<Loadable<?>> getDisabledMods()
    {
        List<Loadable<?>> disabledMods = new ArrayList<Loadable<?>>();

        for (ModInfo<?> disabledMod : this.mods.getDisabledMods())
        {
            disabledMods.add(disabledMod.getContainer());
        }

        return disabledMods;
    }

    /**
     * Get the list of injected tweak containers
     */
    @SuppressWarnings("unchecked")
    public Collection<Loadable<File>> getInjectedTweaks()
    {
        Collection<Loadable<File>> tweaks = new ArrayList<Loadable<File>>();

        for (ModInfo<Loadable<?>> tweak : this.mods.getInjectedTweaks())
        {
            tweaks.add((Loadable<File>)tweak.getContainer());
        }

        return tweaks;
    }

    /**
     * Get a reference to a loaded mod, if the mod exists
     * 
     * @param modName Mod's name, identifier or class name
     * @throws InvalidActivityException
     */
    public <T extends LiteMod> T getMod(String modName) throws InvalidActivityException, IllegalArgumentException
    {
        if (!this.modInitComplete)
        {
            throw new InvalidActivityException("Attempted to get a reference to a mod before loader startup is complete");
        }

        return this.mods.getMod(modName);
    }

    /**
     * Get a reference to a loaded mod, if the mod exists
     * 
     * @param modClass Mod class
     */
    public <T extends LiteMod> T getMod(Class<T> modClass)
    {
        if (!this.modInitComplete)
        {
            throw new RuntimeException("Attempted to get a reference to a mod before loader startup is complete");
        }

        return this.mods.getMod(modClass);
    }

    /**
     * Get whether the specified mod is installed
     *
     * @param modName
     */
    public boolean isModInstalled(String modName)
    {
        if (!this.modInitComplete || modName == null) return false;

        return this.mods.isModInstalled(modName);
    }

    /**
     * Get a metadata value for the specified mod
     * 
     * @param modNameOrId
     * @param metaDataKey
     * @param defaultValue
     * @throws IllegalArgumentException Thrown by getMod if argument is null
     */
    public String getModMetaData(String modNameOrId, String metaDataKey, String defaultValue) throws IllegalArgumentException
    {
        return this.mods.getModMetaData(modNameOrId, metaDataKey, defaultValue);
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
        return this.mods.getModMetaData(mod, metaDataKey, defaultValue);
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
        return this.mods.getModMetaData(modClass, metaDataKey, defaultValue);
    }

    /**
     * Get the mod identifier, this is used for versioning, exclusivity, and
     * enablement checks.
     * 
     * @param modClass
     */
    public String getModIdentifier(Class<? extends LiteMod> modClass)
    {
        return this.mods.getModIdentifier(modClass);
    }

    /**
     * Get the mod identifier, this is used for versioning, exclusivity, and
     * enablement checks.
     * 
     * @param mod
     */
    public String getModIdentifier(LiteMod mod)
    {
        return this.mods.getModIdentifier(mod);
    }

    /**
     * Get the container (mod file, classpath jar or folder) for the specified
     * mod.
     * 
     * @param modClass
     */
    public LoadableMod<?> getModContainer(Class<? extends LiteMod> modClass)
    {
        return this.mods.getModContainer(modClass);
    }

    /**
     * Get the container (mod file, classpath jar or folder) for the specified
     * mod.
     * 
     * @param mod
     */
    public LoadableMod<?> getModContainer(LiteMod mod)
    {
        return this.mods.getModContainer(mod);
    }

    /**
     * Get the mod which matches the specified identifier
     * 
     * @param identifier
     */
    public Class<? extends LiteMod> getModFromIdentifier(String identifier)
    {
        return this.mods.getModFromIdentifier(identifier);
    }

    /**
     * @param identifier Identifier of the mod to enable
     */
    public void enableMod(String identifier)
    {
        this.mods.setModEnabled(identifier, true);
    }

    /**
     * @param identifier Identifier of the mod to disable
     */
    public void disableMod(String identifier)
    {
        this.mods.setModEnabled(identifier, false);
    }

    /**
     * @param identifier Identifier of the mod to enable/disable
     * @param enabled
     */
    public void setModEnabled(String identifier, boolean enabled)
    {
        this.mods.setModEnabled(identifier, enabled);
    }

    /**
     * @param modName
     */
    public boolean isModEnabled(String modName)
    {
        return this.mods.isModEnabled(modName);
    }

    /**
     * @param modName
     */
    public boolean isModActive(String modName)
    {
        return this.mods.isModActive(modName);
    }

    /**
     * @param exposable
     */
    public void writeConfig(Exposable exposable)
    {
        this.configManager.invalidateConfig(exposable);
    }

    /**
     * Register an arbitrary Exposable
     * 
     * @param exposable Exposable object to register
     * @param fileName Override config file name to use (leave null to use value
     *      from ExposableConfig specified value)
     */
    public void registerExposable(Exposable exposable, String fileName)
    {
        this.configManager.registerExposable(exposable, fileName, true);
        this.configManager.initConfig(exposable);
    }

    /**
     * Initialise lifetime objects like the game engine, event broker and
     * interface manager.
     */
    private void initLifetimeObjects()
    {
        // Cache game engine reference
        this.engine = this.objectFactory.getGameEngine();

        // Cache profiler instance
        this.profiler = this.objectFactory.getGameEngine().getProfiler();

        // Create the event broker
        this.events = this.objectFactory.getEventBroker();
        if (this.events != null)
        {
            this.events.setMods(this.mods);
        }

        // Get the mod panel manager
        this.panelManager = this.objectFactory.getPanelManager();
        if (this.panelManager != null)
        {
            this.panelManager.init(this.mods, this.configManager);
        }

        // Create the interface manager
        this.interfaceManager = new LiteLoaderInterfaceManager(this.apiAdapter);
    }

    /**
     * 
     */
    private void postInitCoreProviders()
    {
        this.coreProviders.all().onPostInit(this.engine);

        this.interfaceManager.registerInterfaces();

        for (CoreProvider provider : this.coreProviders)
        {
            if (provider instanceof Listener)
            {
                this.interfaceManager.registerListener((Listener)provider);
            }
        }
    }

    private void loadAndInitMods()
    {
        int totalMods = this.enumerator.modsToLoadCount();
        int totalTweaks = this.enumerator.getInjectedTweaks().size();
        LiteLoaderLogger.info(Verbosity.REDUCED, "Discovered %d total mod(s), injected %d tweak(s)", totalMods, totalTweaks);

        if (totalMods > 0)
        {
            this.mods.loadMods();
            this.mods.initMods();
        }
        else
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "No mod classes were found. Not loading any mods.");
        }

        // Initialises the required hooks for loaded mods
        this.interfaceManager.onPostInit();

        this.modInitComplete = true;
        this.mods.onPostInit();
    }

    void onPostInitMod(LiteMod mod)
    {
        // add mod to permissions manager if permissible
        if (this.permissionsManagerClient != null)
        {
            this.permissionsManagerClient.registerMod(mod);
        }
    }

    /**
     * Called after mod late init
     */
    void onStartupComplete()
    {
        // Set the loader branding in ClientBrandRetriever using reflection
        LiteLoaderBootstrap.setBranding("LiteLoader");

        this.coreProviders.all().onStartupComplete();

        if (this.panelManager != null)
        {
            this.panelManager.onStartupComplete();
        }

        MessageBus.getInstance().onStartupComplete();

        // Force packet injections
        EnumConnectionState.values();
    }

    /**
     * Called on login
     * 
     * @param netHandler
     * @param loginPacket
     */
    void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket)
    {
        if (this.permissionsManagerClient != null)
        {
            this.permissionsManagerClient.onJoinGame(netHandler, loginPacket);
        }

        this.coreProviders.all().onJoinGame(netHandler, loginPacket);
    }

    /**
     * Called when the world reference is changed
     * 
     * @param world
     */
    void onWorldChanged(World world)
    {
        if (world != null && this.permissionsManagerClient != null)
        {
            // For bungeecord
            this.permissionsManagerClient.scheduleRefresh();
        }

        this.worldObservers.all().onWorldChanged(world);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    void onPostRender(int mouseX, int mouseY, float partialTicks)
    {
        this.profiler.startSection("core");
        this.postRenderObservers.all().onPostRender(mouseX, mouseY, partialTicks);
        this.profiler.endSection();
    }

    /**
     * @param clock
     * @param partialTicks
     * @param inGame
     */
    void onTick(boolean clock, float partialTicks, boolean inGame)
    {
        if (clock)
        {
            // Tick the permissions manager
            if (this.permissionsManagerClient != null)
            {
                this.profiler.startSection("permissionsmanager");
                this.permissionsManagerClient.onTick(this.engine, partialTicks, inGame);
                this.profiler.endSection();
            }

            // Tick the config manager
            this.profiler.startSection("configmanager");
            this.configManager.onTick();
            this.profiler.endSection();

            if (!this.engine.isRunning())
            {
                this.onShutDown();
                return;
            }
        }

        this.profiler.startSection("observers");

        this.tickObservers.all().onTick(clock, partialTicks, inGame);

        this.profiler.endSection();
    }

    private void onShutDown()
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "LiteLoader is shutting down, shutting down core providers and syncing configuration");

        this.shutdownObservers.all().onShutDown();

        this.configManager.syncConfig();
    }

    public static String translate(String key, Object... args)
    {
        for (TranslationProvider translator : LiteLoader.instance.translators)
        {
            String translated = translator.translate(key, args);
            if (translated != null)
            {
                return translated;
            }
        }

        return key;
    }

    /**
     * @param objCrashReport This is an object so that we don't need to
     *      transform the obfuscated name in the transformer
     */
    public static void populateCrashReport(Object objCrashReport)
    {
        if (objCrashReport instanceof CrashReport)
        {
            EventProxy.populateCrashReport((CrashReport)objCrashReport);
            LiteLoader.populateCrashReport((CrashReport)objCrashReport);
        }
    }

    private static void populateCrashReport(CrashReport crashReport)
    {
        CrashReportCategory category = crashReport.getCategory(); // crashReport.makeCategoryDepth("Mod System Details", 1);
        category.addCrashSectionCallable("Mod Pack",        new CallableLiteLoaderBrand(crashReport));
        category.addCrashSectionCallable("LiteLoader Mods", new CallableLiteLoaderMods(crashReport));
        category.addCrashSectionCallable("LaunchWrapper",   new CallableLaunchWrapper(crashReport));
    }

    static final void createInstance(LoaderEnvironment environment, LoaderProperties properties, LaunchClassLoader classLoader)
    {
        if (LiteLoader.instance == null)
        {
            LiteLoader.classLoader = classLoader;
            LiteLoader.instance = new LiteLoader(environment, properties);
        }
    }

    static final void invokeInit()
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "LiteLoader begin INIT...");

        LiteLoader.instance.onInit();
    }

    static final void invokePostInit()
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "LiteLoader begin POSTINIT...");

        LiteLoader.instance.onPostInit();
    }
}