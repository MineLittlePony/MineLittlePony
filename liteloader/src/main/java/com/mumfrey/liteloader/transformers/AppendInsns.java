package com.mumfrey.liteloader.transformers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which instructs the ClassOverlayTransformer to append instructions
 * from the annotated method to the target method.
 * 
 * @author Adam Mummery-Smith
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppendInsns
{
    public String value() default("");
}
