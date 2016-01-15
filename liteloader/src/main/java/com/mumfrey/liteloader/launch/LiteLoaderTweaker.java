package com.mumfrey.liteloader.launch;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.spongepowered.asm.launch.MixinBootstrap;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.base.Preconditions;
import com.mumfrey.liteloader.launch.LoaderEnvironment.EnvironmentType;
import com.mumfrey.liteloader.transformers.event.EventInfo;
import com.mumfrey.liteloader.util.SortableValue;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * LiteLoader tweak class
 * 
 * @author Adam Mummery-Smith
 */
public class LiteLoaderTweaker implements ITweaker
{
    public static final int ENV_TYPE_CLIENT = 0;
    public static final int ENV_TYPE_DEDICATEDSERVER = 1;

    // TODO Version - 1.8
    public static final String VERSION = "1.8";

    protected static final String bootstrapClassName = "com.mumfrey.liteloader.core.LiteLoaderBootstrap";

    /**
     * Loader startup state
     * 
     * @author Adam Mummery-Smith
     */
    enum StartupState
    {
        PREPARE,
        PREINIT,
        BEGINGAME,
        INIT,
        POSTINIT,
        DONE;

        /**
         * Current state
         */
        private static StartupState currentState = StartupState.PREPARE.gotoState();

        /**
         * Whether this state is active
         */
        private boolean inState;

        /**
         * Whether this state is completed (can go to next state)
         */
        private boolean completed;

        /**
         * Get whether this state is completed
         */
        public boolean isCompleted()
        {
            return this.completed;
        }

        /**
         * Get whether the tweaker is currrently in this state
         */
        public boolean isInState()
        {
            return this.inState;
        }

        /**
         * Go to the next state, checks whether can move to the next state
         * (previous state is marked completed) first
         */
        public StartupState gotoState()
        {
            for (StartupState otherState : StartupState.values())
            {
                if (otherState.isInState() && otherState != this)
                {
                    if (otherState.canGotoState(this))
                    {
                        otherState.leaveState();
                    }
                    else
                    {
                        String message = String.format("Cannot go to state <%s> as %s %s", this.name(), otherState,
                                otherState.getNextState() == this ? "" : "and expects \""  + otherState.getNextState().name() + "\" instead");
                        throw new IllegalStateException(message, LiteLoaderLogger.getLastThrowable());
                    }
                }
            }

            LiteLoaderLogger.clearLastThrowable();
            StartupState.currentState = this;

            this.inState = true;
            this.completed = false;

            return this;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString()
        {
            return String.format("<%s> is %s %s", this.name(),
                    this.inState ? "[ACTIVE]" : "[INACTIVE]",
                    this.completed ? "and [COMPLETED]" : "but [INCOMPLETE]");
        }

        /**
         * 
         */
        public void leaveState()
        {
            this.inState = false;
        }

        /**
         * 
         */
        public void completed()
        {
            if (!this.inState || this.completed)
            {
                String message = String.format("Attempted to complete state %s but the state is already completed or is not active", this.name());
                throw new IllegalStateException(message, LiteLoaderLogger.getLastThrowable());
            }

            this.completed = true;
        }

        /**
         * Get the state which follows this state
         */
        private StartupState getNextState()
        {
            return this.ordinal() < StartupState.values().length - 1 ? StartupState.values()[this.ordinal() + 1] : StartupState.DONE;
        }

        /**
         * @param next
         */
        public boolean canGotoState(StartupState next)
        {
            if (this.inState && next == this.getNextState())
            {
                return this.completed;
            }

            return !this.inState;
        }

        /**
         * Get the current state
         */
        public static StartupState getCurrent()
        {
            return StartupState.currentState;
        }
    }

    /**
     * Singleton instance, mainly for delegating from injected callbacks which
     * need a static method to call.
     */
    protected static LiteLoaderTweaker instance;

    /**
     * Approximate location of the minecraft jar, used for "base" injection
     * position in ClassPathUtilities.
     */
    protected static URL jarUrl;

    /**
     * "Order" value for inserted tweakers, used as disambiguating sort criteria
     * for injected tweakers which have the same priority. 
     */
    protected int tweakOrder = 0;

    /**
     * All tweakers, used to avoid injecting duplicates
     */
    protected Set<String> allCascadingTweaks = new HashSet<String>();

    /**
     * Sorted list of tweakers, used to sort tweakers before injecting
     */
    protected Set<SortableValue<String>> sortedCascadingTweaks = new TreeSet<SortableValue<String>>();

    /**
     * True if this is the primary tweak, not known until at least PREJOINGAME
     */
    protected boolean isPrimary;

    /**
     * Startup environment information, used to store info about the current
     * startup in one place, also handles parsing command line arguments.
     */
    protected StartupEnvironment env;

    /**
     * Loader bootstrap object
     */
    protected LoaderBootstrap bootstrap;

    /**
     * Transformer manager
     */
    protected ClassTransformerManager transformerManager;

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.ITweaker
     *      #acceptOptions(java.util.List, java.io.File, java.io.File,
     *      java.lang.String)
     */
    @Override
    public void acceptOptions(List<String> args, File gameDirectory, File assetsDirectory, String profile)
    {
        LiteLoaderTweaker.injectTweakClass("org.spongepowered.asm.launch.MixinTweaker");
        
        Launch.classLoader.addClassLoaderExclusion("org.apache.");
        Launch.classLoader.addClassLoaderExclusion("com.google.common.");
        Launch.classLoader.addClassLoaderExclusion("org.objectweb.asm.");
        LiteLoaderTweaker.instance = this;

        this.onPrepare(args, gameDirectory, assetsDirectory, profile);

        this.onPreInit();
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.ITweaker
     *      #injectIntoClassLoader(
     *      net.minecraft.launchwrapper.LaunchClassLoader)
     */
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
//        classLoader.addClassLoaderExclusion("com.mumfrey.liteloader.core.runtime.Obf");
//        classLoader.addClassLoaderExclusion("com.mumfrey.liteloader.core.runtime.Packets");

        this.transformerManager.injectUpstreamTransformers(classLoader);

        for (String transformerClassName : this.bootstrap.getRequiredDownstreamTransformers())
        {
            LiteLoaderLogger.info("Queuing required class transformer '%s'", transformerClassName);
            this.transformerManager.injectTransformer(transformerClassName);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.ITweaker#getLaunchTarget()
     */
    @Override
    public String getLaunchTarget()
    {
        this.isPrimary = true;
        this.onPreBeginGame();

        return "net.minecraft.client.main.Main";
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.ITweaker#getLaunchArguments()
     */
    @Override
    public String[] getLaunchArguments()
    {
        return this.env.getLaunchArguments();
    }

    /**
     * Return true if this is the primary tweaker
     */
    public boolean isPrimary()
    {
        return this.isPrimary;
    }

    /**
     * Get the class transformer manager
     */
    public ClassTransformerManager getTransformerManager()
    {
        return this.transformerManager;
    }

    /**
     * @param args
     * @param gameDirectory
     * @param assetsDirectory
     * @param profile
     */
    private void onPrepare(List<String> args, File gameDirectory, File assetsDirectory, String profile)
    {
        LiteLoaderLogger.info(Verbosity.REDUCED, "Bootstrapping LiteLoader " + LiteLoaderTweaker.VERSION);

        try
        {
            this.initEnvironment(args, gameDirectory, assetsDirectory, profile);

            this.bootstrap = this.spawnBootstrap(LiteLoaderTweaker.bootstrapClassName, Launch.classLoader);

            this.transformerManager = new ClassTransformerManager(this.bootstrap.getRequiredTransformers());

            StartupState.PREPARE.completed();
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error during LiteLoader PREPARE: %s %s", th.getClass().getName(), th.getMessage());
        }
    }

    /**
     * Do the first stage of loader startup, which enumerates mod sources and
     * finds tweakers.
     */
    private void onPreInit()
    {
        StartupState.PREINIT.gotoState();

        try
        {
            MixinBootstrap.init();
            
            this.bootstrap.preInit(Launch.classLoader, true, this.env.getModFilterList());

            this.injectDiscoveredTweakClasses();
            StartupState.PREINIT.completed();
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error during LiteLoader PREINIT: %s %s", th.getClass().getName(), th.getMessage());
        }
    }

    /**
     * 
     */
    private void onPreBeginGame()
    {
        if (StartupState.BEGINGAME.isCompleted())
        {
            return;
        }

        StartupState.BEGINGAME.gotoState();
        try
        {
            this.transformerManager.injectDownstreamTransformers(Launch.classLoader);
            this.bootstrap.preBeginGame();
            MixinBootstrap.addProxy();
            StartupState.BEGINGAME.completed();
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error during LiteLoader BEGINGAME: %s %s", th.getClass().getName(), th.getMessage());
        }
    }

    /**
     * Do the second stage of loader startup
     */
    private void onInit()
    {
        StartupState.INIT.gotoState();

        try
        {
            this.bootstrap.init();
            StartupState.INIT.completed();
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error during LiteLoader INIT: %s %s", th.getClass().getName(), th.getMessage());
        }
    }

    /**
     * Do the second stage of loader startup
     */
    private void onPostInit()
    {
        StartupState.POSTINIT.gotoState();

        try
        {
            this.bootstrap.postInit();
            StartupState.POSTINIT.completed();

            StartupState.DONE.gotoState();
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error during LiteLoader POSTINIT: %s %s", th.getClass().getName(), th.getMessage());
        }
    }

    /**
     * Set up the startup environment
     * 
     * @param args
     * @param gameDirectory
     * @param assetsDirectory
     * @param profile
     */
    private void initEnvironment(List<String> args, File gameDirectory, File assetsDirectory, String profile)
    {
        this.env = this.spawnStartupEnvironment(args, gameDirectory, assetsDirectory, profile);

        URL[] urls = Launch.classLoader.getURLs();
        LiteLoaderTweaker.jarUrl = urls[urls.length - 1]; // probably?
    }

    /**
     * Injects discovered tweak classes
     */
    private void injectDiscoveredTweakClasses()
    {
        if (this.sortedCascadingTweaks.size() > 0)
        {
            if (StartupState.getCurrent() != StartupState.PREINIT || !StartupState.PREINIT.isInState())
            {
                LiteLoaderLogger.warning("Failed to inject cascaded tweak classes because preInit is already complete");
                return;
            }

            LiteLoaderLogger.info("Injecting cascaded tweakers...");

            List<String> tweakClasses = LiteLoaderTweaker.getTweakClasses();
            List<ITweaker> tweakers = LiteLoaderTweaker.getTweakers();
            if (tweakClasses != null && tweakers != null)
            {
                for (SortableValue<String> tweak : this.sortedCascadingTweaks)
                {
                    String tweakClass = tweak.getValue();
                    LiteLoaderLogger.info(Verbosity.REDUCED, "Injecting tweak class %s with priority %d", tweakClass, tweak.getPriority());
                    LiteLoaderTweaker.injectTweakClass(tweakClass, tweakClasses, tweakers);
                }
            }

            // Clear sortedTweaks but not allTweaks
            this.sortedCascadingTweaks.clear();
        }
    }
    
    private static boolean injectTweakClass(String tweakClass)
    {
        List<String> tweakClasses = LiteLoaderTweaker.getTweakClasses();
        List<ITweaker> tweakers = LiteLoaderTweaker.getTweakers();
        return LiteLoaderTweaker.injectTweakClass(tweakClass, tweakClasses, tweakers);
    }

    /**
     * @param tweakClass
     * @param tweakClasses
     * @param tweakers
     */
    private static boolean injectTweakClass(String tweakClass, List<String> tweakClasses, List<ITweaker> tweakers)
    {
        if (tweakClasses.contains(tweakClass))
        {
            return false;
        }
            
        for (ITweaker existingTweaker : tweakers)
        {
            if (tweakClass.equals(existingTweaker.getClass().getName()))
            {
                return false;
            }
        }

        tweakClasses.add(tweakClass);
        return true;
    }

    /**
     * @param tweakClass
     * @param priority
     */
    public boolean addCascadedTweaker(String tweakClass, int priority)
    {
        if (tweakClass != null && !this.allCascadingTweaks.contains(tweakClass))
        {
            if (this.getClass().getName().equals(tweakClass))
            {
                return false;
            }

            if (LiteLoaderTweaker.isTweakAlreadyEnqueued(tweakClass))
            {
                return false;
            }

            this.allCascadingTweaks.add(tweakClass);
            this.sortedCascadingTweaks.add(new SortableValue<String>(priority, this.tweakOrder++, tweakClass));
            return true;
        }

        return false;
    }

    /**
     * The bootstrap object has to be spawned using reflection for obvious
     * reasons 
     * 
     * @param bootstrapClassName
     * @param classLoader
     */
    protected LoaderBootstrap spawnBootstrap(String bootstrapClassName, ClassLoader classLoader)
    {
        if (!StartupState.PREPARE.isInState())
        {
            throw new IllegalStateException("spawnBootstrap is not valid outside PREPARE");
        }

        try
        {
            @SuppressWarnings("unchecked")
            Class<? extends LoaderBootstrap> bootstrapClass = (Class<? extends LoaderBootstrap>)Class.forName(bootstrapClassName, false, classLoader);
            Constructor<? extends LoaderBootstrap> bootstrapCtor = bootstrapClass.getDeclaredConstructor(StartupEnvironment.class, ITweaker.class);
            bootstrapCtor.setAccessible(true);

            return bootstrapCtor.newInstance(this.env, this);
        }
        catch (Throwable th)
        {
            throw new RuntimeException(th);
        }
    }

    /**
     * @param args
     * @param gameDirectory
     * @param assetsDirectory
     * @param profile
     */
    protected StartupEnvironment spawnStartupEnvironment(List<String> args, File gameDirectory, File assetsDirectory, String profile)
    {
        return new StartupEnvironment(args, gameDirectory, assetsDirectory, profile)
        {
            @Override
            public void registerCoreAPIs(List<String> apisToLoad)
            {
                apisToLoad.add(0, "com.mumfrey.liteloader.client.api.LiteLoaderCoreAPIClient");
            }

            @Override
            public int getEnvironmentTypeId()
            {
                return LiteLoaderTweaker.ENV_TYPE_CLIENT;
            }
        };
    }

    /**
     * Get the game jar url (probably)
     */
    public static URL getJarUrl()
    {
        return LiteLoaderTweaker.jarUrl;
    }

    /**
     * @param url URL to add
     */
    public static boolean addURLToParentClassLoader(URL url)
    {
        if (StartupState.getCurrent() == StartupState.PREINIT && StartupState.PREINIT.isInState())
        {
            try
            {
                URLClassLoader classLoader = (URLClassLoader)Launch.class.getClassLoader();
                Method mAddUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                mAddUrl.setAccessible(true);
                mAddUrl.invoke(classLoader, url);

                return true;
            }
            catch (Exception ex)
            {
                LiteLoaderLogger.warning(ex, "addURLToParentClassLoader failed: %s", ex.getMessage());
            }
        }

        return false;
    }

    /**
     * @param clazz
     */
    private static boolean isTweakAlreadyEnqueued(String clazz)
    {
        List<String> tweakClasses = LiteLoaderTweaker.getTweakClasses();
        List<ITweaker> tweakers = LiteLoaderTweaker.getTweakers();

        if (tweakClasses != null)
        {
            for (String tweakClass : tweakClasses)
            {
                if (tweakClass.equals(clazz)) return true;
            }
        }

        if (tweakers != null)
        {
            for (ITweaker tweaker : tweakers)
            {
                if (tweaker.getClass().getName().equals(clazz)) return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private static List<String> getTweakClasses()
    {
        return Preconditions.<List<String>>checkNotNull((List<String>)Launch.blackboard.get("TweakClasses"), "TweakClasses");
    }

    @SuppressWarnings("unchecked")
    private static List<ITweaker> getTweakers()
    {
        return Preconditions.<List<ITweaker>>checkNotNull((List<ITweaker>)Launch.blackboard.get("Tweaks"), "Tweaks");
    }

    /**
     * Get whether to enable the loading bar for minecraft startup
     */
    public static boolean loadingBarEnabled()
    {
        LoaderProperties properties = LiteLoaderTweaker.instance.bootstrap.getProperties();
        return properties != null && properties.getBooleanProperty("loadingbar"); 
    }

    /**
     * Callback from the "Main" class, do the PREBEGINGAME steps (inject
     * "downstream" transformers)
     */
    public static void preBeginGame()
    {
        LiteLoaderTweaker.instance.onPreBeginGame();
    }

    /**
     * Callback from Minecraft::startGame() do early mod initialisation
     */
    public static void init()
    {
        LiteLoaderTweaker.instance.onInit();
    }

    /**
     * Callback from Minecraft::startGame() do late mod initialisation
     */
    public static void postInit()
    {
        LiteLoaderTweaker.instance.onPostInit();
    }

    public static void dedicatedServerInit(EventInfo<?> e)
    {
        LiteLoaderTweaker.instance.onInit();
        LiteLoaderTweaker.instance.onPostInit();
    }
    
    public static EnvironmentType getEnvironmentType()
    {
        return LiteLoaderTweaker.instance.bootstrap.getEnvironment().getType();
    }
}