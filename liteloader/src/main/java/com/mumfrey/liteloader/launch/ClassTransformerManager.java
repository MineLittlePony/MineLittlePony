package com.mumfrey.liteloader.launch;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * Manages injection of required and optional transformers
 * 
 * @author Adam Mummery-Smith
 */
public class ClassTransformerManager
{
    /**
     * Once the game is started we can no longer inject transformers
     */
    private boolean gameStarted;

    /**
     * Transformers to inject after preInit but before the game starts,
     * necessary for anything that needs to be downstream of forge.
     */
    private Set<String> downstreamTransformers = new LinkedHashSet<String>();

    /**
     * Transformers passed into the constructor which are required and must be
     * injected upstream.
     */
    private final List<String> requiredTransformers;

    /**
     * Transformers successfully injected by us
     */
    private final Set<String> injectedTransformers = new LinkedHashSet<String>();

    /**
     * Catalogue of transformer startup failures
     */
    private final Map<String, List<Throwable>> transformerStartupErrors = new HashMap<String, List<Throwable>>();

    private Logger attachedLog;

    private String pendingTransformer;

    class ThrowableObserver extends AbstractAppender
    {
        public ThrowableObserver()
        {
            super("Throwable Observer", null, null);
            this.start();
        }

        @Override
        public void append(LogEvent event)
        {
            ClassTransformerManager.this.observeThrowable(event.getThrown());
        }
    }

    /**
     * @param requiredTransformers
     */
    public ClassTransformerManager(List<String> requiredTransformers)
    {
        this.requiredTransformers = requiredTransformers;

        this.appendObserver();
    }

    private void appendObserver()
    {
        try
        {
            Field fLogger = LogWrapper.class.getDeclaredField("myLog");
            fLogger.setAccessible(true);
            this.attachedLog = (Logger)fLogger.get(LogWrapper.log);
            if (this.attachedLog instanceof org.apache.logging.log4j.core.Logger)
            {
                ((org.apache.logging.log4j.core.Logger)this.attachedLog).addAppender(new ThrowableObserver());
            }
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Failed to append ThrowableObserver to LogWrapper, transformer startup exceptions may not be logged");
        }
    }

    /**
     * @param transformerClass
     */
    public boolean injectTransformer(String transformerClass)
    {
        if (!this.gameStarted)
        {
            this.downstreamTransformers.add(transformerClass);
            return true;
        }

        return false;
    }

    /**
     * @param transformerClasses
     */
    public boolean injectTransformers(Collection<String> transformerClasses)
    {
        if (!this.gameStarted)
        {
            this.downstreamTransformers.addAll(transformerClasses);
            return true;
        }

        return false;
    }

    /**
     * @param transformerClasses
     */
    public boolean injectTransformers(String[] transformerClasses)
    {
        if (!this.gameStarted)
        {
            this.downstreamTransformers.addAll(Arrays.asList(transformerClasses));
            return true;
        }

        return false;
    }

    /**
     * @param classLoader
     */
    void injectUpstreamTransformers(LaunchClassLoader classLoader)
    {
        for (String requiredTransformerClassName : this.requiredTransformers)
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Injecting required class transformer '%s'", requiredTransformerClassName);
            this.injectTransformer(classLoader, requiredTransformerClassName);
        }
    }

    /**
     * @param classLoader
     */
    void injectDownstreamTransformers(LaunchClassLoader classLoader)
    {
        this.gameStarted = true;

        if (this.downstreamTransformers.size() > 0)
        {
            LiteLoaderLogger.info("Injecting downstream transformers");
        }

        for (String transformerClassName : this.downstreamTransformers)
        {
            LiteLoaderLogger.info(Verbosity.REDUCED, "Injecting additional class transformer class '%s'", transformerClassName);
            this.injectTransformer(classLoader, transformerClassName);
        }

        this.downstreamTransformers.clear();
    }

    private synchronized void injectTransformer(LaunchClassLoader classLoader, String transformerClassName)
    {
        try
        {
            // Assign pendingTransformer so that logged errors during transformer init can be put in the map
            this.pendingTransformer = transformerClassName;

            // Register the transformer
            classLoader.registerTransformer(transformerClassName);

            // Unassign pending transformer now init is completed
            this.pendingTransformer = null;

            // Check whether the transformer was successfully injected, look for it in the transformer list
            if (this.findTransformer(classLoader, transformerClassName) != null)
            {
                this.injectedTransformers.add(transformerClassName);
            }
        }
        catch (Throwable th)
        {
            LiteLoaderLogger.severe(th, "Error injecting class transformer class %s", transformerClassName);
        }
    }

    public void observeThrowable(Throwable th)
    {
        if (th != null && this.pendingTransformer != null)
        {
            List<Throwable> transformerErrors = this.transformerStartupErrors.get(this.pendingTransformer);
            if (transformerErrors == null)
            {
                transformerErrors = new ArrayList<Throwable>();
                this.transformerStartupErrors.put(this.pendingTransformer, transformerErrors);
            }
            transformerErrors.add(th);
        }
    }

    private IClassTransformer findTransformer(LaunchClassLoader classLoader, String transformerClassName)
    {
        for (IClassTransformer transformer : classLoader.getTransformers())
        {
            if (transformer.getClass().getName().equals(transformerClassName))
            {
                return transformer;
            }
        }

        return null;
    }

    public Set<String> getInjectedTransformers()
    {
        return Collections.unmodifiableSet(this.injectedTransformers);
    }

    public List<Throwable> getTransformerStartupErrors(String transformerClassName)
    {
        List<Throwable> errorList = this.transformerStartupErrors.get(transformerClassName);
        return errorList != null ? Collections.unmodifiableList(errorList) : null;
    }
}
