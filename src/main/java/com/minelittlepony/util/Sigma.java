package com.minelittlepony.util;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value={METHOD, FIELD, LOCAL_VARIABLE, PARAMETER})
public @interface Sigma {
    public static final @Sigma float LEFT = -1;
    public static final @Sigma float RIGHT = 1;
}
