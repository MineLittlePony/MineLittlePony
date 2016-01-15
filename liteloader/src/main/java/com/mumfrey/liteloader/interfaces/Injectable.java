package com.mumfrey.liteloader.interfaces;

import java.net.MalformedURLException;
import java.net.URL;

import com.mumfrey.liteloader.launch.InjectionStrategy;

import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * Interface for objects which can be injected into the classpath
 *
 * @author Adam Mummery-Smith
 */
public interface Injectable
{
    /**
     * Get the URL of this injectable resource
     * @throws MalformedURLException
     */
    public abstract URL getURL() throws MalformedURLException;

    /**
     * Returns true if this object has been injected already
     */
    public abstract boolean isInjected();

    /**
     * @param classLoader
     * @param injectIntoParent
     * @return whether the injection was successful or not
     * @throws MalformedURLException
     */
    public abstract boolean injectIntoClassPath(LaunchClassLoader classLoader, boolean injectIntoParent) throws MalformedURLException;

    /**
     * Get the injection strategy for this object
     */
    public abstract InjectionStrategy getInjectionStrategy();
}
