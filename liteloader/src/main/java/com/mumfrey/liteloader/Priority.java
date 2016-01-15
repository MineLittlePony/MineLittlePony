package com.mumfrey.liteloader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Priority declaration for LiteMods, used when sorting listener lists. Default
 * value if no Priority annotation is specified is 1000.
 * 
 * @author Adam Mummery-Smith
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority
{
    /**
     * Priority value, default priority is 1000
     */
    public int value();
}
