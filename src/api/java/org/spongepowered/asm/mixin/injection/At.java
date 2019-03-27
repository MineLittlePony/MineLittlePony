package org.spongepowered.asm.mixin.injection;

public @interface At {
    String value() default "";
    String target() default "";
    int ordinal() default 0;
    boolean remap() default true;
    Shift shift() default Shift.BEFORE;
    
    enum Shift {
        BEFORE,
        AFTER
    }
}
