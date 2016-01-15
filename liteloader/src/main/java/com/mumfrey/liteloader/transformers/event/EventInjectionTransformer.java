package com.mumfrey.liteloader.transformers.event;

import com.mumfrey.liteloader.transformers.ObfProvider;

import net.minecraft.launchwrapper.IClassTransformer;

public abstract class EventInjectionTransformer implements IClassTransformer
{
    public EventInjectionTransformer()
    {
        try
        {
            this.addEvents();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.IClassTransformer
     *      #transform(java.lang.String, java.lang.String, byte[])
     */
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        return basicClass;
    }

    /**
     * Subclasses should register events here
     */
    protected abstract void addEvents();

    /**
     * Register a new event to be injected, the event instance will be created
     * if it does not already exist.
     * 
     * @param eventName Name of the event to use/create. Beware that
     *      IllegalArgumentException if the event was already defined with
     *      incompatible parameters
     * @param targetMethod Method descriptor to identify the method to inject
     *      into
     * @param injectionPoint Delegate which finds the location(s) in the target
     *      method to inject into
     * 
     * @return the event - for fluent interface
     */
    protected final Event addEvent(String eventName, MethodInfo targetMethod, InjectionPoint injectionPoint)
    {
        return this.addEvent(Event.getOrCreate(eventName), targetMethod, injectionPoint);
    }

    /**
     * Register an event to be injected
     * 
     * @param event Event to inject
     * @param targetMethod Method descriptor to identify the method to inject
     *      into
     * @param injectionPoint Delegate which finds the location(s) in the target
     *      method to inject into
     * 
     * @return the event - for fluent interface
     */
    protected final Event addEvent(Event event, MethodInfo targetMethod, InjectionPoint injectionPoint)
    {
        if (event == null)
        {
            throw new IllegalArgumentException("Event cannot be null!");
        }

        if (injectionPoint == null)
        {
            throw new IllegalArgumentException("Injection point cannot be null for event " + event.getName());
        }

        if ("true".equals(System.getProperty("mcpenv")))
        {
            EventTransformer.addEvent(event, targetMethod.owner, targetMethod.sig, injectionPoint);
        }
        else
        {
            EventTransformer.addEvent(event, targetMethod.owner, targetMethod.sigSrg, injectionPoint);
            EventTransformer.addEvent(event, targetMethod.ownerObf, targetMethod.sigObf, injectionPoint);
        }

        event.addPendingInjection(targetMethod);

        return event;
    }

    /**
     * Register an access injection interface
     * 
     * @param interfaceName
     */
    protected final void addAccessor(String interfaceName)
    {
        EventTransformer.addAccessor(interfaceName);
    }

    /**
     * Register an access injection interface and provide a contextual
     * obfuscation provider.
     * 
     * @param interfaceName
     * @param obfProvider
     */
    protected final void addAccessor(String interfaceName, ObfProvider obfProvider)
    {
        EventTransformer.addAccessor(interfaceName, obfProvider);
    }
}
