/*package com.minelittlepony.hdskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.hdskins.upload.GLWindow;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

/**
 * I removed it. :T
 *
 * /
@Mixin(value = Minecraft.class, priority = 9000)
public abstract class MixinMinecraft {

    //public void displayCrashReport(CrashReport crashReportIn)
    @Inject(method = "displayCrashReport(Lnet/minecraft/crash/CrashReport;)V", at = @At("HEAD"))
    private void onGameCrash(CrashReport report, CallbackInfo info) {
        GLWindow.dispose();
    }
}
*/