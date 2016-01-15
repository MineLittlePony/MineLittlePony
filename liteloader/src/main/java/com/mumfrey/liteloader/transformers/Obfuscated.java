package com.mumfrey.liteloader.transformers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which provides the obfuscated names for a method or field to the
 * ClassOverlayTransformer.
 *
 * @author Adam Mummery-Smith
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Obfuscated
{
    public String[] value();
}
