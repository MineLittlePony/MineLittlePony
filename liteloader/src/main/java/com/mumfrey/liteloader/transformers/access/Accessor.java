package com.mumfrey.liteloader.transformers.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an accessor method within an accessor injection interface, or an
 * accessor interface itself.
 * 
 * @author Adam Mummery-Smith
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Accessor
{
    public String[] value();
}
