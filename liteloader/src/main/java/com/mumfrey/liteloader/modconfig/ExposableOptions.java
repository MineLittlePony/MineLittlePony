package com.mumfrey.liteloader.modconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which can be a applied to mod classes to indicate that members
 * decorated with the Gson Expose annotation should be serialised with Gson.
 *
 * @author Adam Mummery-Smith
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExposableOptions
{
    /**
     * Configuration strategy to use
     */
    ConfigStrategy strategy() default ConfigStrategy.Unversioned;

    /**
     * Config file name, if not specified the mod class name is used
     */
    String filename() default "";

    /**
     * Set to true to disable write anti-hammer for config file
     */
    boolean aggressive() default false;
}
