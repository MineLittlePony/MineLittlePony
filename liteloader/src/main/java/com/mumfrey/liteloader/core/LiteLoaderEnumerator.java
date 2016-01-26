package com.mumfrey.liteloader.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;

import com.google.common.base.Throwables;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.ContainerRegistry;
import com.mumfrey.liteloader.api.ContainerRegistry.DisabledReason;
import com.mumfrey.liteloader.api.EnumerationObserver;
import com.mumfrey.liteloader.api.EnumeratorModule;
import com.mumfrey.liteloader.api.EnumeratorPlugin;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.ModClassValidator;
import com.mumfrey.liteloader.core.api.DefaultClassValidator;
import com.mumfrey.liteloader.core.api.DefaultEnumeratorPlugin;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.interfaces.Injectable;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.LoaderEnumerator;
import com.mumfrey.liteloader.interfaces.MixinContainer;
import com.mumfrey.liteloader.interfaces.TweakContainer;
import com.mumfrey.liteloader.launch.ClassTransformerManager;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * The enumerator performs all mod discovery functions for LiteLoader, this
 * includes locating mod files to load as well as searching for mod classes
 * within the class path and discovered mod files.
 *
 * @author Adam Mummery-Smith
 */
public class LiteLoaderEnumerator implements LoaderEnumerator
{
    public enum EnumeratorState
    {
        INIT(null),
        DISCOVER(INIT),
        INJECT(DISCOVER),
        REGISTER(INJECT),
        FINALISED(REGISTER);

        private final EnumeratorState previousState;

        private EnumeratorState(EnumeratorState previousState)
        {
            this.previousState = previousState;
        }

        public boolean checkGotoState(EnumeratorState fromState)
        {
            if (fromState != this && fromState != this.previousState)
            {
                throw new IllegalStateException("Attempted to move to an invalid enumerator state " + this + ", expected to be in state "
                        + this.previousState + " but current state is " + fromState);
            }

            return true;
        }
    }

    private final LoaderEnvironment environment;

    private final LoaderProperties properties;

    /**
     * Reference to the launch classloader
     */
    private final LaunchClassLoader classLoader;

    /**
     * 
     */
    private final List<EnumeratorModule> modules = new ArrayList<EnumeratorModule>();

    private final List<EnumeratorPlugin> plugins = new ArrayList<EnumeratorPlugin>();

    private final ContainerRegistry containers = new Containers();

    /**
     * Containers which have already been checked for potential mod candidates 
     */
    private final Set<LoadableMod<?>> enumeratedContainers = new HashSet<LoadableMod<?>>();

    /**
     * Classes to load, mapped by class name 
     */
    private final Set<ModInfo<LoadableMod<?>>> modsToLoad = new LinkedHashSet<ModInfo<LoadableMod<?>>>();

    private final ModClassValidator validator;

    private final FastIterableDeque<EnumerationObserver> observers = new HandlerList<EnumerationObserver>(EnumerationObserver.class);

    protected EnumeratorState state = EnumeratorState.INIT;

    /**
     * @param environment
     * @param properties
     * @param classLoader
     */
    public LiteLoaderEnumerator(LoaderEnvironment environment, LoaderProperties properties, LaunchClassLoader classLoader)
    {
        this.environment       = environment;
        this.properties        = properties;
        this.classLoader       = classLoader;
        this.validator         = this.getValidator(environment);

        this.initModules(environment);
        this.registerPlugin(new DefaultEnumeratorPlugin());

        // Initialise observers
        this.observers.addAll(environment.getAPIAdapter().getPreInitObservers(EnumerationObserver.class));

        // Initialise the shared mod list if we haven't already
        this.getSharedModList();
    }

    /**
     * @param environment
     */
    private ModClassValidator getValidator(LoaderEnvironment environment)
    {
        List<String> prefixes = new ArrayList<String>();

        for (LiteAPI api : environment.getAPIProvider().getAPIs())
        {
            String prefix = api.getModClassPrefix();
            if (prefix != null)
            {
                LiteLoaderLogger.info("Adding supported mod class prefix '%s'", prefix);
                prefixes.add(prefix);
            }
        }

        return new DefaultClassValidator<LiteMod>(LiteMod.class, prefixes);
    }

    /**
     * @param environment
     */
    private void initModules(LoaderEnvironment environment)
    {
        for (LiteAPI api : environment.getAPIProvider().getAPIs())
        {
            List<EnumeratorModule> apiModules = api.getEnumeratorModules();

            if (apiModules != null)
            {
                for (EnumeratorModule module : apiModules)
                {
                    this.registerModule(module);
                }
            }
        }
    }

    private void checkState(EnumeratorState state, String action)
    {
        if (this.state != state)
        {
            throw new IllegalStateException("Illegal enumerator state whilst performing " + action + ", expecting " + state + " but current state is "
                    + this.state);
        }
    }

    private void gotoState(EnumeratorState state)
    {
        if (state.checkGotoState(this.state))
        {
            this.state = state;
        }
    }

    /**
     * Get the loader environment
     */
    public LoaderEnvironment getEnvironment()
    {
        return this.environment;
    }

    /**
     * Initialise the "shared" mod list if it's not already been created
     */
    @Override
    public Map<String, Map<String, String>> getSharedModList()
    {
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Map<String,String>> sharedModList = (Map<String, Map<String, String>>) Launch.blackboard.get("modList");

            if (sharedModList == null)
            {
                sharedModList = new HashMap<String, Map<String,String>>();
                Launch.blackboard.put("modList", sharedModList);
            }

            return sharedModList;
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Shared mod list was invalid or not accessible, this isn't especially bad but something isn't quite right");
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.PluggableEnumerator
     *      #registerModule(com.mumfrey.liteloader.core.EnumeratorModule)
     */
    @Override
    public void registerModule(EnumeratorModule module)
    {
        this.checkState(EnumeratorState.INIT, "registerModule");

        if (module != null && !this.modules.contains(module))
        {
            LiteLoaderLogger.info("Registering discovery module %s: [%s]", module.getClass().getSimpleName(), module);
            this.modules.add(module);
            module.init(this.environment, this.properties);
        }
    }

    @Override
    public void registerPlugin(EnumeratorPlugin plugin)
    {
        this.checkState(EnumeratorState.INIT, "registerPlugin");

        if (plugin != null && !this.plugins.contains(plugin))
        {
            LiteLoaderLogger.info("Registering enumerator plugin %s: [%s]", plugin.getClass().getSimpleName(), plugin);
            this.plugins.add(plugin);
            plugin.init(this.environment, this.properties);
        }
    }

    /**
     * Get the list of all enumerated mod classes to load
     */
    @Override
    public Collection<? extends ModInfo<LoadableMod<?>>> getModsToLoad()
    {
        this.checkState(EnumeratorState.FINALISED, "getModsToLoad");
        return Collections.unmodifiableSet(this.modsToLoad);
    }

    /**
     * Get the set of disabled containers
     */
    @Override
    public Collection<? extends ModInfo<Loadable<?>>> getDisabledContainers()
    {
        this.checkState(EnumeratorState.FINALISED, "getDisabledContainers");
        return this.containers.getDisabledContainers();
    }

    @Override
    public Collection<? extends ModInfo<Loadable<?>>> getBadContainers()
    {
        this.checkState(EnumeratorState.FINALISED, "getBadContainers");
        return this.containers.getBadContainers();
    }

    /**
     * Get the list of injected tweak containers
     */
    @Override
    public Collection<? extends ModInfo<Loadable<?>>> getInjectedTweaks()
    {
        this.checkState(EnumeratorState.FINALISED, "getInjectedTweaks");
        return this.containers.getInjectedTweaks();
    }

    /**
     * Get the number of mods to load
     */
    @Override
    public int modsToLoadCount()
    {
        return this.modsToLoad.size();
    }

    /**
     * Get a metadata value for the specified mod
     * 
     * @param modClass
     * @param metaDataKey
     * @param defaultValue
     */
    @Override
    public String getModMetaData(Class<? extends LiteMod> modClass, String metaDataKey, String defaultValue)
    {
        this.checkState(EnumeratorState.FINALISED, "getModMetaData");
        return this.getContainerForMod(modClass).getMetaValue(metaDataKey, defaultValue);
    }

    /**
     * @param identifier
     */
    @Override
    public LoadableMod<?> getContainer(String identifier)
    {
        this.checkState(EnumeratorState.FINALISED, "getContainer");
        return this.containers.getEnabledContainer(identifier);
    }

    /**
     * @param modClass
     */
    @Override
    public LoadableMod<?> getContainer(Class<? extends LiteMod> modClass)
    {
        this.checkState(EnumeratorState.FINALISED, "getContainer");
        return this.getContainerForMod(modClass);
    }

    /**
     * @param modClass
     */
    private LoadableMod<?> getContainerForMod(Class<? extends LiteMod> modClass)
    {
        for (ModInfo<LoadableMod<?>> mod : this.modsToLoad)
        {
            if (modClass.equals(mod.getModClass()))
            {
                return mod.getContainer();
            }
        }

        return LoadableMod.NONE;
    }

    /**
     * Get the mod identifier (metadata key), this is used for versioning,
     * exclusivity, and enablement checks.
     * 
     * @param modClass
     */
    @Override
    public String getIdentifier(Class<? extends LiteMod> modClass)
    {
        String modClassName = modClass.getSimpleName();

        for (ModInfo<LoadableMod<?>> mod : this.modsToLoad)
        {
            if (modClassName.equals(mod.getModClassSimpleName()))
            {
                return mod.getIdentifier();
            }
        }

        return LiteLoaderEnumerator.getModClassName(modClass);
    }

    @Override
    public void onPreInit()
    {
        this.discoverContainers();
        this.injectDiscoveredTweaks();
    }

    /**
     * Call enumerator modules in order to find mod containers
     */
    private void discoverContainers()
    {
        this.gotoState(EnumeratorState.DISCOVER);

        for (EnumeratorModule module : this.modules)
        {
            try
            {
                module.enumerate(this, this.environment.getProfile());
            }
            catch (Throwable th)
            {
                LiteLoaderLogger.warning(th, "Enumerator Module %s encountered an error whilst enumerating", module.getClass().getName());
            }
        }

        this.checkDependencies();
    }

    private void injectDiscoveredTweaks()
    {
        this.gotoState(EnumeratorState.INJECT);

        for (TweakContainer<File> tweakContainer : this.containers.getTweakContainers())
        {
            this.addTweaksFrom(tweakContainer);
        }
    }

    /**
     * Enumerate class path and discovered mod files to find mod classes
     */
    @Override
    public void onInit()
    {
        try
        {
            this.gotoState(EnumeratorState.INJECT);
            this.injectIntoClassLoader();

            this.gotoState(EnumeratorState.REGISTER);
            this.registerMods();

            this.gotoState(EnumeratorState.FINALISED);
            LiteLoaderLogger.info("Mod class discovery completed");
        }
        catch (IllegalStateException ex) // wut?
        {
            Throwables.propagate(ex);
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.warning(th, "Mod class discovery failed");
        }
    }

    /**
     * 
     */
    private void injectIntoClassLoader()
    {
        for (EnumeratorModule module : this.modules)
        {
            try
            {
                module.injectIntoClassLoader(this, this.classLoader);
            }
            catch (Throwable th)
            {
                LiteLoaderLogger.warning(th, "Enumerator Module %s encountered an error whilst injecting", module.getClass().getName());
            }
        }
    }

    /**
     * 
     */
    private void registerMods()
    {
        for (EnumeratorModule module : this.modules)
        {
            try
            {
                module.registerMods(this, this.classLoader);
            }
            catch (Throwable th)
            {
                LiteLoaderLogger.warning(th, "Enumerator Module %s encountered an error whilst registering mods", module.getClass().getName());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.interfaces.ModularEnumerator
     *      #registerModContainer(com.mumfrey.liteloader.interfaces.LoadableMod)
     */
    @Override
    public final boolean registerModContainer(LoadableMod<?> container)
    {
        this.checkState(EnumeratorState.DISCOVER, "registerModContainer");

        if (container == null)
        {
            return true;
        }

        if (!this.checkEnabled(container))
        {
            this.registerDisabledContainer(container, DisabledReason.USER_DISABLED);
            return false;
        }

        if (!this.checkAPIRequirements(container))
        {
            this.registerDisabledContainer(container, DisabledReason.MISSING_API);
            return false;
        }

        this.registerEnabledContainer(container);
        return true;
    }

    @Override
    public void registerBadContainer(Loadable<?> container, String reason)
    {
        this.checkState(EnumeratorState.DISCOVER, "registerBadContainer");
        this.containers.registerBadContainer(container, reason);
    }

    /**
     * @param container
     */
    protected void registerEnabledContainer(LoadableMod<?> container)
    {
        this.checkState(EnumeratorState.DISCOVER, "registerEnabledContainer");
        this.containers.registerEnabledContainer(container);
        this.observers.all().onRegisterEnabledContainer(this, container);
    }

    /**
     * @param container
     */
    protected void registerDisabledContainer(LoadableMod<?> container, DisabledReason reason)
    {
        this.checkState(EnumeratorState.DISCOVER, "registerDisabledContainer");

        LiteLoaderLogger.info(Verbosity.REDUCED, reason.getMessage(container));
        this.containers.registerDisabledContainer(container, reason);
        this.observers.all().onRegisterDisabledContainer(this, container, reason);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.PluggableEnumerator#addTweaksFrom(
     *      com.mumfrey.liteloader.core.TweakContainer)
     */
    @Override
    public boolean registerTweakContainer(TweakContainer<File> container)
    {
        this.checkState(EnumeratorState.DISCOVER, "registerTweakContainer");

        if (!container.isEnabled(this.environment))
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Mod %s is disabled for profile %s, not injecting tranformers",
                    container.getIdentifier(), this.environment.getProfile());
            return false;
        }

        this.containers.registerTweakContainer(container);
        this.observers.all().onRegisterTweakContainer(this, container);
        return true;
    }

    /**
     * @param tweakContainer
     */
    private void addTweaksFrom(TweakContainer<File> tweakContainer)
    {
        this.checkState(EnumeratorState.INJECT, "addTweaksFrom");

        if (this.checkDependencies(tweakContainer))
        {
            if (tweakContainer.hasTweakClass())
            {
                this.addTweakFrom(tweakContainer);
            }

            if (tweakContainer.hasClassTransformers())
            {
                this.addClassTransformersFrom(tweakContainer);
            }
            
            if (tweakContainer.hasMixins())
            {
                this.addMixinsFrom(tweakContainer);
            }
        }
    }

    private void addTweakFrom(TweakContainer<File> container)
    {
        try
        {
            String tweakClass = container.getTweakClassName();
            int tweakPriority = container.getTweakPriority();
            LiteLoaderLogger.info(Verbosity.REDUCED, "Mod file '%s' provides tweakClass '%s', adding to Launch queue with priority %d",
                    container.getName(), tweakClass, tweakPriority);
            if (this.environment.addCascadedTweaker(tweakClass, tweakPriority))
            {
                LiteLoaderLogger.info(Verbosity.REDUCED, "tweakClass '%s' was successfully added", tweakClass);
                container.injectIntoClassPath(this.classLoader, true);

                if (container.isExternalJar())
                {
                    this.containers.registerInjectedTweak(container);
                }

                String[] classPathEntries = container.getClassPathEntries();
                if (classPathEntries != null)
                {
                    for (String classPathEntry : classPathEntries)
                    {
                        try
                        {
                            File classPathJar = new File(this.environment.getGameDirectory(), classPathEntry);
                            URL classPathJarUrl = classPathJar.toURI().toURL();

                            LiteLoaderLogger.info("Adding Class-Path entry: %s", classPathEntry); 
                            LiteLoaderTweaker.addURLToParentClassLoader(classPathJarUrl);
                            this.classLoader.addURL(classPathJarUrl);
                        }
                        catch (MalformedURLException ex) {}
                    }
                }
            }
        }
        catch (MalformedURLException ex)
        {
        }
    }

    private void addClassTransformersFrom(TweakContainer<File> container)
    {
        try
        {
            for (String classTransformerClass : container.getClassTransformerClassNames())
            {
                LiteLoaderLogger.info(Verbosity.REDUCED, "Mod file '%s' provides classTransformer '%s', adding to class loader",
                        container.getName(), classTransformerClass);
                ClassTransformerManager transformerManager = this.environment.getTransformerManager();
                if (transformerManager != null && transformerManager.injectTransformer(classTransformerClass))
                {
                    LiteLoaderLogger.info(Verbosity.REDUCED, "classTransformer '%s' was successfully added", classTransformerClass);
                    this.injectContainerRecursive(container);
                }
            }
        }
        catch (MalformedURLException ex)
        {
        }
    }

    private void addMixinsFrom(MixinContainer<File> container)
    {
        try {
        container.injectIntoClassPath(this.classLoader, true);
        for (String config : container.getMixinConfigs())
        {
            if (config.endsWith(".json"))
            {
                LiteLoaderLogger.info(Verbosity.REDUCED, "Registering mixin config %s for %s", config, container.getName());
                MixinEnvironment.getDefaultEnvironment().addConfiguration(config);
            }
            else if (config.contains(".json@"))
            {
                int pos = config.indexOf(".json@");
                String phaseName = config.substring(pos + 6);
                config = config.substring(0, pos + 5);
                Phase phase = Phase.forName(phaseName);
                if (phase != null)
                {
                    LiteLoaderLogger.info(Verbosity.REDUCED, "Registering mixin config %s for %s", config, container.getName());
                    MixinEnvironment.getEnvironment(phase).addConfiguration(config);
                }
            }
        }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * @param container
     */
    private void injectContainerRecursive(Injectable container) throws MalformedURLException
    {
        if (container.injectIntoClassPath(this.classLoader, true) && container instanceof LoadableMod)
        {
            LoadableMod<?> file = (LoadableMod<?>)container;
            for (String dependency : file.getDependencies())
            {
                LoadableMod<?> dependencyContainer = this.containers.getEnabledContainer(dependency);
                if (dependencyContainer != null)
                {
                    this.injectContainerRecursive(dependencyContainer);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.PluggableEnumerator#registerMods(
     *      com.mumfrey.liteloader.core.LoadableMod, boolean)
     */
    @Override
    public void registerModsFrom(LoadableMod<?> container, boolean registerContainer)
    {
        this.checkState(EnumeratorState.REGISTER, "registerModsFrom");

        if (this.containers.isDisabledContainer(container))
        {
            throw new IllegalArgumentException("Attempted to register mods from a disabled container '" + container.getName() + "'");
        }

        if (this.enumeratedContainers.contains(container))
        {
            // already handled this container
            return;
        }

        this.enumeratedContainers.add(container);

        List<Class<? extends LiteMod>> modClasses = new ArrayList<Class<? extends LiteMod>>();

        for (EnumeratorPlugin plugin : this.plugins)
        {
            List<Class<? extends LiteMod>> classes = plugin.getClasses(container, this.classLoader, this.validator);
            LiteLoaderLogger.debug("Plugin %s returned %d classes for %s", plugin.getClass(), classes.size(), container.getDisplayName());
            modClasses.addAll(classes);
        }

        for (Class<? extends LiteMod> modClass : modClasses)
        {
            Mod mod = new Mod(container, modClass);
            this.registerMod(mod);
        }

        if (modClasses.size() > 0)
        {
            LiteLoaderLogger.info("Found %d potential matches", modClasses.size());
            this.containers.registerEnabledContainer(container);
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.interfaces.ModularEnumerator#registerMod(
     *      com.mumfrey.liteloader.interfaces.ModInfo)
     */
    @Override
    public void registerMod(ModInfo<LoadableMod<?>> mod)
    {
        this.checkState(EnumeratorState.REGISTER, "registerMod");

        if (this.modsToLoad.contains(mod))
        {
            LiteLoaderLogger.warning("Mod name collision for mod with class '%s', maybe you have more than one copy?", mod.getModClassSimpleName());
        }

        this.modsToLoad.add(mod);

        this.observers.all().onModAdded(this, mod);
    }

    private boolean checkEnabled(LoadableMod<?> container)
    {
        for (EnumeratorPlugin plugin : this.plugins)
        {
            if (!plugin.checkEnabled(this.containers, container)) return false;
        }

        return true;
    }

    @Override
    public boolean checkAPIRequirements(LoadableMod<?> container)
    {
        for (EnumeratorPlugin plugin : this.plugins)
        {
            if (!plugin.checkAPIRequirements(this.containers, container)) return false;
        }

        return true;
    }

    /**
     * Check dependencies of enabled containers
     */
    private void checkDependencies()
    {
        Collection<? extends LoadableMod<?>> enabledContainers = this.containers.getEnabledContainers();
        Deque<LoadableMod<?>> containers = new LinkedList<LoadableMod<?>>(enabledContainers);

        while (containers.size() > 0)
        {
            LoadableMod<?> container = containers.pop();
            if (!this.checkDependencies(container))
            {
                this.registerDisabledContainer(container, DisabledReason.MISSING_DEPENDENCY);

                // Iterate so that a container disabled by a failed dependency check will also
                // disable any containers which depend upon it
                containers.clear();
                containers.addAll(enabledContainers);
            }
        }
    }

    @Override
    public boolean checkDependencies(LoadableMod<?> container)
    {
        for (EnumeratorPlugin plugin : this.plugins)
        {
            if (!plugin.checkDependencies(this.containers, container)) return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean checkDependencies(TweakContainer<File> tweakContainer)
    {
        if (tweakContainer instanceof LoadableMod)
        {
            return this.checkDependencies((LoadableMod<File>)tweakContainer);
        }

        return true;
    }

    public static String getModClassName(LiteMod mod)
    {
        return LiteLoaderEnumerator.getModClassName(mod.getClass());
    }

    public static String getModClassName(Class<? extends LiteMod> mod)
    {
        return mod.getSimpleName().substring(7);
    }
}