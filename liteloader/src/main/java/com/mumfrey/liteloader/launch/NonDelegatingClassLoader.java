package com.mumfrey.liteloader.launch;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * ClassLoader which only allows whitelisted classes to be loaded, used to
 * pre-load packet transformer classes to ensure that they don't reference any
 * external classes.
 *
 * @author Adam Mummery-Smith
 */
public class NonDelegatingClassLoader extends URLClassLoader
{
    /**
     * Class names which we can load with this loader 
     */
    private final Set<String> validClassNames = new HashSet<String>();

    /**
     * Packages which we can load with this loader
     */
    private final Set<String> validPackages = new HashSet<String>();

    /**
     * Class names which will be forcibly delegated to the parent ClassLoader 
     */
    private final Set<String> delegatedClassNames = new HashSet<String>();

    /**
     * Package names which will be forcibly delegated to the parent ClassLoader 
     */
    private final Set<String> delegatedPackages = new HashSet<String>();

    private final ClassLoader parent;

    private boolean valid = true;

    private String invalidClassName = null;

    NonDelegatingClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls, null);

        this.parent = parent;

        this.validClassNames.add("java.lang.Object");
        this.validPackages.add("java.");
    }

    public boolean isValid()
    {
        return this.valid;
    }

    public String getInvalidClassName()
    {
        return this.invalidClassName;
    }

    public void reset()
    {
        this.valid = true;
        this.invalidClassName = null;
    }

    public void addValidClassName(String className)
    {
        this.validClassNames.add(className);
    }

    public void addValidPackage(String packageName)
    {
        if (!packageName.endsWith(".")) packageName += ".";
        this.validPackages.add(packageName);
    }

    public void addDelegatedClassName(String className)
    {
        this.delegatedClassNames.add(className);
        this.validClassNames.add(className);
    }

    public void addDelegatedPackage(String packageName)
    {
        if (!packageName.endsWith(".")) packageName += ".";
        this.delegatedPackages.add(packageName);
        this.validPackages.add(packageName);
    }

    public Class<?> addAndLoadClass(String name) throws ClassNotFoundException
    {
        this.reset();
        this.addValidClassName(name);
        return this.loadClass(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (this.parent != null)
        {
            if (this.delegatedClassNames.contains(name))
            {
                return this.parent.loadClass(name);
            }

            for (String delegatedPackage : this.delegatedPackages)
            {
                if (name.startsWith(delegatedPackage))
                {
                    return this.parent.loadClass(name);
                }
            }
        }

        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        if (name == null) return null;

        if (this.validClassNames.contains(name))
        {
            return super.findClass(name);
        }

        for (String validPackage : this.validPackages)
        {
            if (name.startsWith(validPackage))
            {
                return super.findClass(name);
            }
        }

        this.valid = false;
        this.invalidClassName = name;

        return super.findClass(name);
    }
}
