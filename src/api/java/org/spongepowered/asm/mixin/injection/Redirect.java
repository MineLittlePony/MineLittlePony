package org.spongepowered.asm.mixin.injection;

public @interface Redirect {
    String[] method() default "";
    At at();
}
