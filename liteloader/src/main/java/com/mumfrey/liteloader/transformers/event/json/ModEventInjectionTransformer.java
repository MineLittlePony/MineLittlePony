package com.mumfrey.liteloader.transformers.event.json;

import com.mumfrey.liteloader.transformers.ClassTransformer;
import com.mumfrey.liteloader.transformers.ObfProvider;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.json.ModEvents.ModEventDefinition;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * Event transformer which manages injections of mod events specified via
 * events.json in the mod container.
 *
 * @author Adam Mummery-Smith
 */
public class ModEventInjectionTransformer extends EventInjectionTransformer
{
    @Override
    protected void addEvents()
    {
        for (ModEventDefinition eventsDefinition : ModEvents.getEvents().values())
        {
            this.addEvents(eventsDefinition);
        }
    }

    /**
     * @param identifier
     * @param json
     */
    private void addEvents(ModEventDefinition def)
    {
        JsonEvents events = null;

        try
        {
            LiteLoaderLogger.info("Parsing events for mod with id %s", def.getIdentifier());
            events = JsonEvents.parse(def.getJson());
        }
        catch (InvalidEventJsonException ex)
        {
            LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
            LiteLoaderLogger.debug(ex.getMessage());
            LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
            LiteLoaderLogger.debug(def.getJson());
            LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
            LiteLoaderLogger.severe(ex, "Invalid JSON event declarations for mod with id %s", def.getIdentifier());
        }
        catch (Throwable ex)
        {
            LiteLoaderLogger.severe(ex, "Error whilst parsing event declarations for mod with id %s", def.getIdentifier());
        }

        try
        {
            if (events != null)
            {
                if (events.hasAccessors())
                {
                    LiteLoaderLogger.info("%s contains Accessor definitions, injecting into classpath...", def.getIdentifier());
                    def.injectIntoClassPath();
                }

                LiteLoaderLogger.info(Verbosity.REDUCED, "Registering events for mod with id %s", def.getIdentifier());
                events.register(this);
                def.onEventsInjected();
            }
        }
        catch (Throwable ex)
        {
            LiteLoaderLogger.severe(ex, "Error whilst parsing event declarations for mod with id %s", def.getIdentifier());
        }
    }

    protected Event registerEvent(Event event, MethodInfo targetMethod, InjectionPoint injectionPoint)
    {
        return super.addEvent(event, targetMethod, injectionPoint);
    }

    protected void registerAccessor(String interfaceName, ObfProvider obfProvider)
    {
        super.addAccessor(interfaceName, obfProvider);
    }
}
