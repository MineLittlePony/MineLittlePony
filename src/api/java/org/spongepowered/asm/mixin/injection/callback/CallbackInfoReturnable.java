package org.spongepowered.asm.mixin.injection.callback;

public abstract class CallbackInfoReturnable<T> {
    public abstract T getReturnValue();
    public abstract void setReturnValue(T t);
}
