package com.mumfrey.liteloader.api;

/**
 * LiteLoader Extensible API - Listener is the base interface for
 * (counter-intuitively) consumable Listener interfaces, in that derived
 * interfaces are consumable (from the point of view of the providers) but
 * actual implementors consume the events thus advertised by implementing those
 * interfaces, making them themselves the consumers. Okay so that's probably
 * pretty confusing but I can't think of any better terminology so it's
 * staying :) 
 * 
 * @author Adam Mummery-Smith
 */
public interface Listener
{
    /**
     * Get the display name
     * 
     * @return display name
     */
    public abstract String getName();
}
