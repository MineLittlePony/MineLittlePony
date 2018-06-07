package com.voxelmodpack.hdskins.mixin;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.voxelmodpack.hdskins.gui.GLWindow;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    //public void displayCrashReport(CrashReport crashReportIn)
    @Inject(method = "displayCrashReport(Lnet/minecraft/crash/CrashReport;)V", at = @At("HEAD"))
    private void onGameCrash(CrashReport report, CallbackInfo info) {
        GLWindow.dispose();
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void onInit(CallbackInfo info) throws LWJGLException, IOException {
        GLWindow.current();
    }
}
