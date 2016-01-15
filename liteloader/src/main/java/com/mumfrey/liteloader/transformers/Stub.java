package com.mumfrey.liteloader.transformers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface which instructs the ClassOverlayTransformer to NOT merge the
 * annotated method.
 *
 * @author Adam Mummery-Smith
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Stub
{
}
