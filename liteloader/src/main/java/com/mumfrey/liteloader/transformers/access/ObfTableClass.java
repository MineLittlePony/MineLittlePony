package com.mumfrey.liteloader.transformers.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * Defines the obfuscation table class to use for an accessor injection
 * interface.
 * 
 * @author Adam Mummery-Smith
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ObfTableClass
{
    public Class<? extends Obf> value();
}
