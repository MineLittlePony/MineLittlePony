package org.spongepowered.asm.mixin.injection;

public @interface ModifyArg {
    String method() default "";
    At at();
    int index();
}
