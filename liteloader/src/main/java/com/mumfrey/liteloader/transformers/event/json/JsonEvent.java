package com.mumfrey.liteloader.transformers.event.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;

/**
 * An event definition in JSON, serialisable class read by Gson
 * 
 * @author Adam Mummery-Smith
 */
public class JsonEvent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static int nextEventID = 0;

    /**
     * Event name
     */
    @SerializedName("name")
    private String name;

    /**
     * Whether the event is cancellable
     */
    @SerializedName("cancellable")
    private boolean cancellable;

    /**
     * Event priority (relative to other events at the same injection point)
     */
    @SerializedName("priority")
    private int priority = 1000;

    /**
     * Injection points specified in the JSON file
     */
    @SerializedName("injections")
    private List<JsonInjection> jsonInjections;

    /**
     * Listeners defined in the JSON file
     */
    @SerializedName("listeners")
    private List<String> jsonListeners;

    /**
     * Listener methods parsed from the JSON
     */
    private transient List<MethodInfo> listeners = new ArrayList<MethodInfo>();

    /**
     * Get the name of this event
     */
    public String getName()
    {
        if (this.name == null)
        {
            this.name = "onUserEvent" + (JsonEvent.nextEventID++);
        }

        return this.name;
    }

    /**
     * Get whether this event is cancellable or not
     */
    public boolean isCancellable()
    {
        return this.cancellable;
    }

    /**
     * Get the event priority
     */
    public int getPriority()
    {
        return this.priority;
    }

    /**
     * Get the list of listeners parsed from the JSON
     */
    public List<MethodInfo> getListeners()
    {
        return this.listeners;
    }

    /**
     * Parse the JSON to initialise this object
     */
    public void parse(JsonMethods methods)
    {
        this.parseInjectionPoints(methods);
        this.parseListeners(methods);
    }

    /**
     * @param methods
     */
    private void parseInjectionPoints(JsonMethods methods)
    {
        if (this.jsonInjections == null || this.jsonInjections.isEmpty())
        {
            throw new InvalidEventJsonException("Event " + this.getName() + " does not have any defined injections");
        }

        for (JsonInjection injection : this.jsonInjections)
        {
            injection.parse(methods);
        }
    }

    /**
     * @param methods
     */
    private void parseListeners(JsonMethods methods)
    {
        if (this.jsonListeners == null || this.jsonListeners.isEmpty())
        {
            throw new InvalidEventJsonException("Event " + this.getName() + " does not have any defined listeners");
        }

        for (String listener : this.jsonListeners)
        {
            this.listeners.add(methods.get(listener));
        }
    }

    /**
     * @param transformer Transformer to register events with
     * @return Event which was registered
     */
    public Event register(ModEventInjectionTransformer transformer)
    {
        Event event = Event.getOrCreate(this.getName(), this.isCancellable(), this.getPriority());

        for (JsonInjection injection : this.jsonInjections)
        {
            MethodInfo targetMethod = injection.getMethod();
            InjectionPoint injectionPoint = injection.getInjectionPoint();

            transformer.registerEvent(event, targetMethod, injectionPoint);
        }

        for (MethodInfo listener : this.listeners)
        {
            event.addListener(listener);
        }

        return event;
    }
}
