package org.spongepowered.asm.mixin.injection;

public @interface Inject {
    String method() default "";
    At at();
    boolean cancellable() default false;
}
