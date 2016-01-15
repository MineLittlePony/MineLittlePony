package com.mumfrey.liteloader.launch;

/**
 * Encapsulates a strategy for injecting a URL into the classpath
 *
 * @author Adam Mummery-Smith
 */
public final class InjectionStrategy
{
    /**
     * Defines a position for a classpath injection strategy
     *
     * @author Adam Mummery-Smith
     */
    public enum InjectionPosition
    {
        /**
         * Inject the URL at the bottom (end) of the classpath, lowest priority
         * - this is the default.
         */
        Bottom,

        /**
         * Inject the URL at the base of the classpath (directly above the
         * minecraft jar but below all other libs).
         */
        Base,

        /**
         * Inject the URL at the top (start) of the classpath, highest priority
         * above all other libs.
         */
        Top,

        /**
         * Inject the URL above the entry which matches the URL defined by param
         */
        Above;

        /**
         * Parse an InjectionPosition from the specified "injectAt" string
         * 
         * @param injectAt
         */
        public static InjectionPosition parsePosition(String injectAt)
        {
            if ("top".equalsIgnoreCase(injectAt)) return InjectionPosition.Top;
            if ("base".equalsIgnoreCase(injectAt)) return InjectionPosition.Base;
            if (injectAt != null && injectAt.toLowerCase().startsWith("above:")) return InjectionPosition.Above;
            return InjectionPosition.Bottom;
        }

        /**
         * Parse InjectionPosition params from the specified "injectAt" string
         * 
         * @param injectAt
         */
        public String[] parseParams(String injectAt)
        {
            if (this == InjectionPosition.Above && injectAt != null) return injectAt.substring(6).split(",");
            return null;
        }
    }

    /**
     * Top strategy
     */
    public static final InjectionStrategy TOP = new InjectionStrategy(InjectionPosition.Top, null);

    /**
     * Default strategy
     */
    public static final InjectionStrategy DEFAULT = new InjectionStrategy(InjectionPosition.Bottom, null);

    /**
     * Position for this strategy
     */
    private final InjectionPosition position;

    /**
     * Params for the strategy (if supported by the specified position)
     */
    private final String[] params;

    /**
     * Private constructor because strategy should be created from a string
     * using parseStrategy()
     * 
     * @param injectAt
     */
    private InjectionStrategy(String injectAt)
    {
        this.position = InjectionPosition.parsePosition(injectAt);
        this.params = this.position.parseParams(injectAt);
    }

    /**
     * Private constructor for the pre-defined public strategies TOP and DEFAULT
     * 
     * @param position
     * @param params
     */
    private InjectionStrategy(InjectionPosition position, String[] params)
    {
        this.position = position;
        this.params = params;
    }

    /**
     * Get the position
     */
    public InjectionPosition getPosition()
    {
        return this.position;
    }

    /**
     * Get the parameters
     */
    public String[] getParams()
    {
        return this.params;
    }

    /**
     * Parse an injection strategy from the specified injectAt string
     * 
     * @param injectAt
     */
    public static InjectionStrategy parseStrategy(String injectAt)
    {
        return InjectionStrategy.parseStrategy(injectAt, null);
    }

    /**
     * Parse an injection strategy from the specified injectAt string
     * 
     * @param injectAt
     */
    public static InjectionStrategy parseStrategy(String injectAt, InjectionStrategy defaultStrategy)
    {
        if (injectAt == null) return defaultStrategy;
        return new InjectionStrategy(injectAt);
    }
}
