package com.voxelmodpack.common.interfaces;

/**
 * Interface for mods which want to interact with inbound time
 *
 * @author Adam Mummery-Smith
 */
public interface ITimeHandler {
    /**
     * Called for every registered handler when a new time packet is received,
     * lets each handler note the REAL (server) time
     * 
     * @param totalTime
     * @param worldTime
     */
    public abstract void onTimeUpdate(long totalTime, long worldTime);

    /**
     * Called for every registered handler until a handler returns TRUE. If a
     * handler returns true then getFrozenTotalTime and getFrozenWorldTime are
     * called and the frozen time overrides all other time adjustments
     */
    public abstract boolean isFreezingTime();

    /**
     * If this handler returns TRUE in isFreezingTime then this method must
     * return the frozen time
     * 
     * @param totalTime
     * @return
     */
    public abstract long getFrozenTotalTime(long totalTime);

    /**
     * If this handler returns TRUE in isFreezingTime then this method must
     * return the frozen time
     * 
     * @param worldTime
     * @return
     */
    public abstract long getFrozenWorldTime(long worldTime);

    /**
     * If time is not frozen, this method should return the amount of time
     * offset this handler wishes to apply
     */
    public abstract long getTimeOffset();
}
