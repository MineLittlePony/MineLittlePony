package com.mumfrey.liteloader.launch;

/**
 * Interface for the object which will manage loader properties (internal and
 * volatile).
 * 
 * @author Adam Mummery-Smith
 */
public interface LoaderProperties
{
    /**
     * True if the "load tweaks" option is enabled and enumerator modules 
     */
    public abstract boolean loadTweaksEnabled();

    /**
     * Get the mod pack branding from the non-volatile store
     */
    public abstract String getBranding();

    /**
     * Set a boolean property in the properties file
     * 
     * @param propertyName
     * @param value
     */
    public abstract void setBooleanProperty(String propertyName, boolean value);

    /**
     * Get a boolean property from the properties file
     * 
     * @param propertyName
     */
    public abstract boolean getBooleanProperty(String propertyName);

    /**
     * Get a boolean property but write and return the supplied default value if
     * the property doesn't exist
     * 
     * @param propertyName
     * @param defaultValue
     */
    public abstract boolean getAndStoreBooleanProperty(String propertyName, boolean defaultValue);

    /**
     * Set an integer property in the properties file
     * 
     * @param propertyName
     * @param value
     */
    public abstract void setIntegerProperty(String propertyName, int value);

    /**
     * Get an integer property from the properties file
     * 
     * @param propertyName
     */
    public abstract int getIntegerProperty(String propertyName);

    /**
     * Get an integer property but write and return the supplied default value
     * if the property doesn't exist
     * 
     * @param propertyName
     * @param defaultValue
     */
    public abstract int getAndStoreIntegerProperty(String propertyName, int defaultValue);

    /**
     * Get a stored mod revision number from the properties file
     * 
     * @param modKey
     */
    public abstract int getLastKnownModRevision(String modKey);

    /**
     * Store a mod revision number in the properties file
     * 
     * @param modKey
     */
    public abstract void storeLastKnownModRevision(String modKey);

    /**
     * Write the properties to disk
     */
    public abstract void writeProperties();

    // General properties
    public static final String OPTION_SOUND_MANAGER_FIX = "soundManagerFix";
    public static final String OPTION_MOD_INFO_SCREEN   = "modInfoScreen";
    public static final String OPTION_NO_HIDE_TAB       = "tabAlwaysExpanded";
    public static final String OPTION_BRAND             = "brand";
    public static final String OPTION_LOADING_BAR       = "loadingbar";
    public static final String OPTION_FORCE_UPDATE      = "allowForceUpdate";
    public static final String OPTION_UPDATE_CHECK_INTR = "updateCheckInterval";
    public static final String OPTION_JINPUT_DISABLE    = "disableJInput";

    // Enumerator properties
    public static final String OPTION_SEARCH_MODS       = "search.mods";
    public static final String OPTION_SEARCH_CLASSPATH  = "search.classpath";
    public static final String OPTION_SEARCH_JARFILES   = "search.jarfiles";
    public static final String OPTION_FORCE_INJECTION   = "forceInjection";
}
