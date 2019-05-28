package com.minelittlepony.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.client.MineLPClient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.util.NonBlockingThreadExecutor;
import net.minecraft.util.snooper.SnooperListener;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends NonBlockingThreadExecutor<Runnable> implements SnooperListener, WindowEventHandler, AutoCloseable {

    public MixinMinecraftClient() { super(null); }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void onInit(CallbackInfo info) {
        MineLPClient.getInstance().postInit(MinecraftClient.getInstance());
    }
}
