package com.mumfrey.liteloader.core.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mumfrey.liteloader.api.ContainerRegistry;
import com.mumfrey.liteloader.api.EnumeratorPlugin;
import com.mumfrey.liteloader.api.ModClassValidator;
import com.mumfrey.liteloader.api.manager.APIProvider;
import com.mumfrey.liteloader.core.exceptions.OutdatedLoaderException;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

public class DefaultEnumeratorPlugin implements EnumeratorPlugin
{
    private LoaderEnvironment environment;

    @Override
    public void init(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.environment = environment;
    }

    @Override
    public boolean checkEnabled(ContainerRegistry containers, LoadableMod<?> container)
    {
        return container.isEnabled(this.environment);
    }

    @Override
    public boolean checkAPIRequirements(ContainerRegistry containers, LoadableMod<?> container)
    {
        boolean result = true;
        APIProvider apiProvider = this.environment.getAPIProvider();

        for (String identifier : container.getRequiredAPIs())
        {
            if (!apiProvider.isAPIAvailable(identifier))
            {
                container.registerMissingAPI(identifier);
                result = false;
            }
        }

        return result;
    }

    @Override
    public boolean checkDependencies(ContainerRegistry containers, LoadableMod<?> base)
    {
        if (base == null || !base.hasDependencies()) return true;

        HashSet<String> circularDependencySet = new HashSet<String>();
        circularDependencySet.add(base.getIdentifier());

        boolean result = this.checkDependencies(containers, base, base, circularDependencySet);
        LiteLoaderLogger.info(Verbosity.REDUCED, "Dependency check for %s %s", base.getIdentifier(), result ? "passed" : "failed");

        return result;
    }

    private boolean checkDependencies(ContainerRegistry containers, LoadableMod<?> base, LoadableMod<?> container, Set<String> circularDependencySet)
    {
        if (container.getDependencies().size() == 0)
        {
            return true;
        }

        boolean result = true;

        for (String dependency : container.getDependencies())
        {
            if (!circularDependencySet.contains(dependency))
            {
                circularDependencySet.add(dependency);

                LoadableMod<?> dependencyContainer = containers.getEnabledContainer(dependency);
                if (dependencyContainer != LoadableMod.NONE)
                {
                    String identifier = dependency;
                    if (this.environment.getEnabledModsList().isEnabled(this.environment.getProfile(), identifier))
                    {
                        result &= this.checkDependencies(containers, base, dependencyContainer, circularDependencySet);
                    }
                    else
                    {
//                        LiteLoaderLogger.warning("Dependency %s required by %s is currently disabled", dependency, base.getIdentifier());
                        base.registerMissingDependency(dependency);
                        result = false;
                    }
                }
                else
                {
//                    LiteLoaderLogger.info("Dependency %s for %s is was not located, no container ", dependency, base.getIdentifier());
                    base.registerMissingDependency(dependency);
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * Enumerate classes on the classpath which are subclasses of the specified
     * class
     */
    @Override
    public <T> List<Class<? extends T>> getClasses(LoadableMod<?> container, ClassLoader classloader, ModClassValidator validator)
    {
        List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();

        if (container != null)
        {
            try
            {
                for (String fullClassName : container.getContainedClassNames())
                {
                    boolean isDefaultPackage = fullClassName.lastIndexOf('.') == -1;
                    String className = isDefaultPackage ? fullClassName : fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                    if (validator.validateName(className))
                    {
                        Class<? extends T> clazz = DefaultEnumeratorPlugin.<T>checkClass(classloader, validator, fullClassName);
                        if (clazz != null && !classes.contains(clazz))
                        {
                            classes.add(clazz);
                        }
                    }
                }
            }
            catch (OutdatedLoaderException ex)
            {
                classes.clear();
                LiteLoaderLogger.info(Verbosity.REDUCED, "Error searching in '%s', missing API component '%s', your loader is probably out of date",
                        container, ex.getMessage());
            }
            catch (Throwable th)
            {
                LiteLoaderLogger.warning(th, "Enumeration error");
            }
        }

        return classes;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> checkClass(ClassLoader classLoader, ModClassValidator validator, String className)
            throws OutdatedLoaderException
    {
        if (className.indexOf('$') > -1)
        {
            return null;
        }

        try
        {
            Class<?> candidateClass = classLoader.loadClass(className);

            if (validator.validateClass(classLoader, candidateClass))
            {
                return (Class<? extends T>)candidateClass;
            }
        }
        catch (Throwable th)
        {
            th.printStackTrace();

            if (th.getCause() != null)
            {
                String missingClassName = th.getCause().getMessage();
                if (th.getCause() instanceof NoClassDefFoundError && missingClassName != null)
                {
                    if (missingClassName.startsWith("com/mumfrey/liteloader/"))
                    {
                        throw new OutdatedLoaderException(missingClassName.substring(missingClassName.lastIndexOf('/') + 1));
                    }
                }
            }

            LiteLoaderLogger.warning(th, "checkAndAddClass error while checking '%s'", className);
        }

        return null;
    }
}
