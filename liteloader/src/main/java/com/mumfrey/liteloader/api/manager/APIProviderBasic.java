package com.mumfrey.liteloader.api.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;

import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.MixinConfigProvider;
import com.mumfrey.liteloader.api.Observer;
import com.mumfrey.liteloader.interfaces.InterfaceRegistry;

/**
 * Basic implementation of APIProvider and APIAdapter
 * 
 * @author Adam Mummery-Smith
 */
class APIProviderBasic implements APIProvider, APIAdapter
{
    /**
     * API instances
     */
    private final LiteAPI[] apis;

    /**
     * Map of API identifiers to API instances
     */
    private final Map<String, LiteAPI> apiMap = new HashMap<String, LiteAPI>();

    /**
     * Cached observer set
     */
    private final Map<LiteAPI, List<? extends Observer>> observers = new HashMap<LiteAPI, List<? extends Observer>>();

    /**
     * Cached preinit observers 
     */
    private final Map<LiteAPI, List<? extends Observer>> preInitiObservers = new HashMap<LiteAPI, List<? extends Observer>>();

    /**
     * Cached CoreProvider set
     */
    private List<CoreProvider> coreProviders;

    APIProviderBasic(LiteAPI[] apis)
    {
        this.apis = apis;

        for (LiteAPI api : this.apis)
        {
            this.apiMap.put(api.getIdentifier(), api);
        }
    }
    
    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIAdapter#initMixins()
     */
    @Override
    public void initMixins()
    {
        for (LiteAPI api : this.apis)
        {
            MixinConfigProvider mixins = api.getMixins();
            if (mixins != null)
            {
                CompatibilityLevel level = mixins.getCompatibilityLevel();
                if (level != null)
                {
                    MixinEnvironment.setCompatibilityLevel(level);
                }
                
                String[] configs = mixins.getMixinConfigs();
                if (configs != null)
                {
                    for (String config : configs)
                    {
                        MixinEnvironment.getDefaultEnvironment().addConfiguration(config);
                    }
                }
                
                String[] errorHandlers = mixins.getErrorHandlers();
                if (errorHandlers != null)
                {
                    for (String handlerName : errorHandlers)
                    {
                        MixinEnvironment.getDefaultEnvironment().registerErrorHandlerClass(handlerName);
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #getRequiredTransformers()
     */
    @Override
    public List<String> getRequiredTransformers()
    {
        List<String> requiredTransformers = new ArrayList<String>();

        for (LiteAPI api : this.apis)
        {
            String[] apiTransformers = api.getRequiredTransformers();
            if (apiTransformers != null)
            {
                requiredTransformers.addAll(Arrays.asList(apiTransformers));
            }
        }

        return requiredTransformers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #getRequiredDownstreamTransformers()
     */
    @Override
    public List<String> getRequiredDownstreamTransformers()
    {
        List<String> requiredDownstreamTransformers = new ArrayList<String>();

        for (LiteAPI api : this.apis)
        {
            String[] apiTransformers = api.getRequiredDownstreamTransformers();
            if (apiTransformers != null)
            {
                requiredDownstreamTransformers.addAll(Arrays.asList(apiTransformers));
            }
        }

        return requiredDownstreamTransformers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #getObservers(com.mumfrey.liteloader.api.LiteAPI)
     */
    @Override
    public List<? extends Observer> getObservers(LiteAPI api)
    {
        if (!this.observers.containsKey(api))
        {
            List<Observer> apiObservers = api.getObservers();
            this.observers.put(api, Collections.unmodifiableList(apiObservers != null ? apiObservers : new ArrayList<Observer>()));
        }

        return this.observers.get(api);
    }

    @Override
    public List<? extends Observer> getPreInitObservers(LiteAPI api)
    {
        if (!this.preInitiObservers.containsKey(api))
        {
            List<Observer> apiObservers = api.getPreInitObservers();
            this.preInitiObservers.put(api, Collections.unmodifiableList(apiObservers != null ? apiObservers : new ArrayList<Observer>()));
        }

        return this.preInitiObservers.get(api);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Observer> List<T> getObservers(LiteAPI api, Class<T> observerType)
    {
        List<T> matchingObservers = new ArrayList<T>();

        for (Observer observer : this.getObservers(api))
        {
            if (observerType.isAssignableFrom(observer.getClass()) && !matchingObservers.contains(observer))
            {
                matchingObservers.add((T)observer);
            }
        }

        return matchingObservers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Observer> List<T> getAllObservers(Class<T> observerType)
    {
        List<T> matchingObservers = new ArrayList<T>();
        for (LiteAPI api : this.apis)
        {
            matchingObservers.addAll(this.<T>getObservers(api, observerType));
        }

        for (CoreProvider coreProvider : this.getCoreProviders())
        {
            if (observerType.isAssignableFrom(coreProvider.getClass()) && !matchingObservers.contains(coreProvider))
            {
                matchingObservers.add((T)coreProvider);
            }
        }

        return matchingObservers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Observer> List<T> getPreInitObservers(Class<T> observerType)
    {
        List<T> matchingObservers = new ArrayList<T>();
        for (LiteAPI api : this.apis)
        {
            for (Observer observer : this.getPreInitObservers(api))
            {
                if (observerType.isAssignableFrom(observer.getClass()) && !matchingObservers.contains(observer))
                {
                    matchingObservers.add((T)observer);
                }
            }
        }

        return matchingObservers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #registerInterfaceProviders(
     *      com.mumfrey.liteloader.core.InterfaceManager)
     */
    @Override
    public void registerInterfaces(InterfaceRegistry interfaceManager)
    {
        for (LiteAPI api : this.apis)
        {
            interfaceManager.registerAPI(api);
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIAdapter#getCoreProviders()
     */
    @Override
    public List<CoreProvider> getCoreProviders()
    {
        if (this.coreProviders == null)
        {
            List<CoreProvider> coreProviders = new ArrayList<CoreProvider>();

            for (LiteAPI api : this.apis)
            {
                List<CoreProvider> apiCoreProviders = api.getCoreProviders();
                if (apiCoreProviders != null)
                {
                    coreProviders.addAll(apiCoreProviders);
                }
            }

            this.coreProviders = Collections.unmodifiableList(coreProviders);
        }

        return this.coreProviders;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider#getAPIs()
     */
    @Override
    public LiteAPI[] getAPIs()
    {
        return this.apis;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #isAPIAvailable(java.lang.String)
     */
    @Override
    public boolean isAPIAvailable(String identifier)
    {
        if (identifier != null && identifier.contains("@"))
        {
            Matcher idAndRevisionPatternMatcher = APIProvider.idAndRevisionPattern.matcher(identifier);
            if (idAndRevisionPatternMatcher.matches())
            {
                return this.isAPIAvailable(idAndRevisionPatternMatcher.group(1), Integer.parseInt(idAndRevisionPatternMatcher.group(2)));
            }
        }

        return this.apiMap.containsKey(identifier);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #isAPIAvailable(java.lang.String, int)
     */
    @Override
    public boolean isAPIAvailable(String identifier, int minRevision)
    {
        LiteAPI api = this.apiMap.get(identifier);
        if (api == null) return false;

        return api.getRevision() >= minRevision;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #getAPI(java.lang.String)
     */
    @Override
    public LiteAPI getAPI(String identifier)
    {
        return this.apiMap.get(identifier);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.manager.APIProvider
     *      #getAPI(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends LiteAPI> T getAPI(Class<T> apiClass)
    {
        try
        {
            for (LiteAPI api : this.apis)
            {
                if (apiClass.isAssignableFrom(api.getClass()))
                {
                    return (T)api;
                }
            }
        }
        catch (NullPointerException ex1) {}
        catch (ClassCastException ex2) {}

        return null;
    }
}
