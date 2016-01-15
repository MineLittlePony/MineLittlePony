package com.mumfrey.liteloader.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.api.InterfaceObserver;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.Observer;
import com.mumfrey.liteloader.api.exceptions.InvalidProviderException;
import com.mumfrey.liteloader.api.manager.APIAdapter;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.interfaces.FastIterable;
import com.mumfrey.liteloader.interfaces.InterfaceRegistry;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * The interface manager handles the allocation of interface consumers
 * (implementors) to interface providers. During startup, registered providers
 * are enumerated and handler mappings are created for every consumable
 * interface the provider supports. Later on, consumers are enumerated against
 * the available handler mappings and registered with the providers by calling
 * the appropriate registration method. 
 *
 * @author Adam Mummery-Smith
 */
public class LiteLoaderInterfaceManager implements InterfaceRegistry
{
    static int handlerAllocationOrder = 0;

    /**
     * InterfaceHandler describes a mapping of a consumable interface to an
     * InterfaceProvider instance and appropriate consumer registration method
     * (which will be invoked via reflection). 
     * 
     * @author Adam Mummery-Smith
     */
    class InterfaceHandler
    {
        /**
         * Priority, for sorting handlers, NYI
         */
        public final int priority;

        /**
         * Order, for sorting handlers, NYI
         */
        public final int order;

        /**
         * Indicates that this handler must be the exclusive hander for this
         * interface
         */
        public final boolean exclusive;

        /**
         * Interface Provider which handles this mapping
         */
        public final InterfaceProvider provider;

        /**
         * Type of interface for this mapping
         */
        public final Class<? extends Listener> interfaceType;

        /**
         * List of registered listeners, so we can avoid registering the same
         * listener multiple times
         */
        private final List<Listener> registeredListeners = new ArrayList<Listener>();

        /**
         * Callback method used to 
         */
        private final Method registrationMethod;

        /**
         * @param provider
         * @param interfaceType
         * @param exclusive
         * @param priority
         */
        public InterfaceHandler(InterfaceProvider provider, Class<? extends Listener> interfaceType, boolean exclusive, int priority)
        {
            this.provider           = provider;
            this.interfaceType      = interfaceType;
            this.exclusive          = exclusive;
            this.priority           = priority;
            this.order              = LiteLoaderInterfaceManager.handlerAllocationOrder++;
            this.registrationMethod = this.findRegistrationMethod(provider, interfaceType);
        }

        /**
         * @param provider
         * @param interfaceType
         */
        @SuppressWarnings("unchecked")
        private Method findRegistrationMethod(InterfaceProvider provider, Class<? extends Listener> interfaceType)
        {
            Method registrationMethod = null;

            Class<? extends InterfaceProvider> providerClass = provider.getClass();
            while (registrationMethod == null && providerClass != null)
            {
                registrationMethod = this.findRegistrationMethod(providerClass, interfaceType);
                providerClass = (Class<? extends InterfaceProvider>)providerClass.getSuperclass();
            }

            return registrationMethod;
        }

        private Method findRegistrationMethod(Class<? extends InterfaceProvider> providerClass, Class<? extends Listener> interfaceType)
        {
            for (Method method : providerClass.getDeclaredMethods())
            {
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(interfaceType))
                {
                    LiteLoaderLogger.debug("Found method %s for registering %s with provider %s",
                            method.getName(), interfaceType, providerClass.getSimpleName());
                    return method;
                }
            }

            return null;
        }

        /**
         * After instantiation, called to check that a valid registration method
         * was located
         */
        public boolean isValid()
        {
            return this.registrationMethod != null;
        }

        /**
         * Proxy method which calls the registration method in the
         * InterfaceProvider using reflection
         * 
         * @param listener
         */
        public boolean registerListener(Listener listener)
        {
            if (this.interfaceType.isAssignableFrom(listener.getClass()) && this.provider.getListenerBaseType().isAssignableFrom(listener.getClass()))
            {
                if (this.registeredListeners.contains(listener))
                {
                    return false;
                }

                try
                {
                    LiteLoaderLogger.debug("Calling registration method %s for %s on %s with %s", this.registrationMethod.getName(),
                            this.interfaceType.getSimpleName(), this.provider.getClass().getSimpleName(), listener.getClass().getSimpleName());
                    this.registrationMethod.invoke(this.provider, listener);

                    this.registeredListeners.add(listener);

                    LiteLoaderInterfaceManager.this.observers.all().onRegisterListener(this.provider, this.interfaceType, listener);

                    return true;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            return false;
        }
    }

    /**
     * API Provider instance
     */
    private final APIAdapter apiAdapter;

    /**
     * Map of providers to the API which supplied them
     */
    private final Map<InterfaceProvider, LiteAPI> providerToAPIMap = new HashMap<InterfaceProvider, LiteAPI>();

    /**
     * All providers
     */
    private final List<InterfaceProvider> allProviders = new ArrayList<InterfaceProvider>();

    /**
     * All consumers
     */
    private final List<Listener> listeners = new ArrayList<Listener>();

    /**
     * Registered interface handler mappings
     */
    private final List<InterfaceHandler> interfaceHandlers = new ArrayList<InterfaceHandler>();

    /**
     * Interface observers
     */
    protected final FastIterable<InterfaceObserver> observers = new HandlerList<InterfaceObserver>(InterfaceObserver.class);

    /**
     * True once the initial init phase (in which all registered providers are
     * initialised) is completed, we use this flag to indicate that any <b>new
     * </b> providers should be immediately initialised.
     */
    private boolean initDone = false;

    /**
     * The last startup phase causes all currently registered consumers to be
     * enumerated and offered to all currently registered listeners, once this
     * initial registration is done any <b>new</b> consumers should immediately
     * offered to all registered listeners.
     */
    private boolean registrationDone = false;

    /**
     * Registratiob Delegate which is active for the current registration
     * process.
     */
    private InterfaceRegistrationDelegate activeRegistrationDelegate;

    /**
     * @param apiAdapter
     */
    LiteLoaderInterfaceManager(APIAdapter apiAdapter)
    {
        this.apiAdapter = apiAdapter;
    }

    /**
     * Callback from the core
     */
    void onPostInit()
    {
        this.registerQueuedListeners();
        this.initProviders();
    }

    void registerInterfaces()
    {
        this.apiAdapter.registerInterfaces(this);
    }

    /**
     * @param api
     */
    @Override
    public void registerAPI(LiteAPI api)
    {
        List<InterfaceProvider> apiInterfaceProviders = api.getInterfaceProviders();
        if (apiInterfaceProviders != null)
        {
            for (InterfaceProvider provider : apiInterfaceProviders)
            {
                LiteLoaderLogger.info(Verbosity.REDUCED, "Registering interface provider %s for API %s",
                        provider.getClass().getName(), api.getName());
                if (this.registerProvider(provider))
                {
                    this.providerToAPIMap.put(provider, api);
                }
            }
        }

        List<? extends Observer> observers = this.apiAdapter.getObservers(api);

        if (observers != null)
        {
            for (Observer observer : observers)
            {
                if (observer instanceof InterfaceObserver)
                {
                    this.registerObserver((InterfaceObserver)observer);
                }
            }
        }
    }

    /**
     * Register a new interface provider
     * 
     * @param provider
     */
    public boolean registerProvider(InterfaceProvider provider)
    {
        if (provider != null && !this.allProviders.contains(provider))
        {
            try
            {
                if (this.activeRegistrationDelegate != null)
                {
                    throw new IllegalStateException("registerProvider() was called whilst a registration process was still active");
                }

                InterfaceRegistrationDelegate delegate = new InterfaceRegistrationDelegate(this, provider);
                this.activeRegistrationDelegate = delegate;
                this.activeRegistrationDelegate.registerInterfaces();
                this.activeRegistrationDelegate = null;

                if (this.initDone)
                {
                    provider.initProvider();
                }

                this.allProviders.add(provider);

                this.interfaceHandlers.addAll(delegate.getHandlers());

                return true;
            }
            catch (Throwable th)
            {
                LiteLoaderLogger.warning(th, "Error while registering interface provider %s: %s",
                        provider.getClass().getSimpleName(), th.getClass().getSimpleName());
            }
        }

        this.activeRegistrationDelegate = null;
        return false;
    }

    /**
     * @param observer
     */
    public void registerObserver(InterfaceObserver observer)
    {
        this.observers.add(observer);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.interfaces.InterfaceRegistry
     *      #registerInterface(
     *      com.mumfrey.liteloader.api.InterfaceProvider, java.lang.Class)
     */
    @Override
    public void registerInterface(InterfaceProvider provider, Class<? extends Listener> interfaceType)
    {
        this.registerInterface(provider, interfaceType, 0);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.interfaces.InterfaceRegistry
     *      #registerInterface(com.mumfrey.liteloader.api.InterfaceProvider,
     *      java.lang.Class, int)
     */
    @Override
    public void registerInterface(InterfaceProvider provider, Class<? extends Listener> interfaceType, int priority)
    {
        this.registerInterface(provider, interfaceType, priority, false);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.interfaces.InterfaceRegistry
     *      #registerInterface(com.mumfrey.liteloader.api.InterfaceProvider,
     *      java.lang.Class, int, boolean)
     */
    @Override
    public void registerInterface(InterfaceProvider provider, Class<? extends Listener> interfaceType, int priority, boolean exclusive)
    {
        InterfaceHandler handler = new InterfaceHandler(provider, interfaceType, exclusive, priority);
        if (handler.isValid())
        {
            // Check if a this provider is already registered
            if (this.getProvidersFor(interfaceType).contains(provider))
            {
                throw new InvalidProviderException("Attempting to register duplicate mapping for provider "
                        + provider.getClass() + " to " + interfaceType);
            }

            if (exclusive)
            {
                this.removeHandlersFor(interfaceType, priority);
            }

            if (this.registrationDone)
            {
                this.interfaceHandlers.add(handler);

                for (Listener consumer : this.listeners)
                {
                    handler.registerListener(consumer);
                }
            }
            else if (this.activeRegistrationDelegate != null)
            {
                this.activeRegistrationDelegate.addHandler(handler);
            }
        }
        else
        {
            throw new InvalidProviderException("Provider " + provider.getClass() + " does not expose a registration method for " + interfaceType);
        }
    }

    /**
     * @param interfaceType
     */
    public List<InterfaceProvider> getProvidersFor(Class<? extends Listener> interfaceType)
    {
        List<InterfaceProvider> handlers = new ArrayList<InterfaceProvider>();

        for (InterfaceHandler handler : this.interfaceHandlers)
        {
            if (handler.interfaceType == interfaceType)
            {
                handlers.add(handler.provider);
            }
        }

        if (this.activeRegistrationDelegate != null)
        {
            for (InterfaceHandler handler : this.activeRegistrationDelegate.getHandlers())
            {
                if (handler.interfaceType == interfaceType)
                {
                    handlers.add(handler.provider);
                }
            }
        }

        return handlers;
    }

    /**
     * @param interfaceType
     * @param priority
     */
    private void removeHandlersFor(Class<? extends Listener> interfaceType, int priority)
    {
        Iterator<InterfaceHandler> iter = this.interfaceHandlers.iterator();
        while (iter.hasNext())
        {
            InterfaceHandler handler = iter.next();
            if (handler.interfaceType.equals(interfaceType))
            {
                if (handler.exclusive)
                {
                    throw new RuntimeException("Attempt to register an exclusive handler when an exclusive handler already exists for "
                            + interfaceType);
                }

                iter.remove();
            }
        }
    }

    /**
     * Returns the API which supplied a particular provider, if the provider was
     * supplied by an API, otherwise returns null.
     * 
     * @param provider
     */
    public LiteAPI getAPIForProvider(InterfaceProvider provider)
    {
        return this.providerToAPIMap.get(provider);
    }

    /**
     * Initialises all registered providers
     */
    private void initProviders()
    {
        if (this.initDone) return;
        this.initDone = true;

        for (InterfaceProvider provider : this.allProviders)
        {
            provider.initProvider();
        }
    }

    /**
     * Offers an interface listener to the manager, the listener will actually
     * be registered with the interface handlers at the end of the startup
     * process.
     * 
     * @param listener
     */
    public void offer(Listener listener)
    {
        if (listener instanceof InterfaceProvider)
        {
            this.registerProvider((InterfaceProvider)listener);
        }

        this.listeners.add(listener);

        if (this.registrationDone)
        {
            this.registerListener(listener);
        }
    }

    /**
     * Registers all enqueued consumers as listeners
     */
    private void registerQueuedListeners()
    {
        for (Listener consumer : this.listeners)
        {
            this.registerListener(consumer);
        }

        this.registrationDone = true;
    }

    /**
     * Registers a listener with all registered handlers
     * 
     * @param listener
     */
    public void registerListener(Listener listener)
    {
        for (InterfaceHandler handler : this.interfaceHandlers)
        {
            handler.registerListener(listener);
        }
    }
}
