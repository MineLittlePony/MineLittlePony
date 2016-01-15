package com.mumfrey.liteloader.api;

/**
 * Generic Observer class, for Intra-API Observer inking
 * 
 * @author Adam Mummery-Smith
 *
 * @param <T> Argument type for observable events
 */
public interface GenericObserver<T> extends Observer
{
    public abstract void onObservableEvent(String eventName, T... eventArgs) throws ClassCastException;
}
