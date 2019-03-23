package com.minelittlepony.hdskins.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The remote server API level that this skin server implements.
 *
 * Current values are:
 *  - legacy
 *  - valhalla
 *  - bethlehem
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerType {

    String value();
}
