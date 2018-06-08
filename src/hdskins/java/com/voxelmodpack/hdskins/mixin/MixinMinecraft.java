package com.voxelmodpack.hdskins.mixin;

import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.voxelmodpack.hdskins.IMinecraft;
import com.voxelmodpack.hdskins.gui.GLWindow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.crash.CrashReport;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {

    @Accessor("mcDefaultResourcePack")
    public abstract DefaultResourcePack getDefaultResourcePack();

    //
    // Due to how JFrame works the only way to know for sure when the game hash crashed
    // is to have it call us explicitly.
    //
    // ShutdownListener.onShutDown is unlikely to be called as it depends on the
    // Minecraft.running flag to be unset, which is unlikely to happen if the game crashes.
    //
    // Runtime.current().addShutdownHook won't be called it waits for all
    // non-daemon threads to end, one of which is depending on the JFrame being
    // disposed to tell it when to end.
    //
    // If you're thinking 'hey, what about Minecraft.isCrashed?'
    // No, that's only set if the internal MinecraftServer crashes.
    // Otherwise the value is always false and threads spinning to check any such value
    // will only serve to hang up the VM.
    //
    // @forge
    //  Because the minecraft forge team are stupid, they call displayCrashReport on startup
    //  regardless of whether the game has crashed or not. Thus the window may flicker an additional
    //  time as the native window is forced back to the front.
    //  This is a minor issue as the window will simply reassert itself when it's next referenced
    //  (i.e. The skins GUI uses it for file drops) so I have no intention of fixing this.
    //
    //  This is their problem.
    //
    //public void displayCrashReport(CrashReport crashReportIn)
    @Inject(method = "displayCrashReport(Lnet/minecraft/crash/CrashReport;)V", at = @At("HEAD"))
    private void onGameCrash(CrashReport report, CallbackInfo info) {
        GLWindow.dispose();
    }

    //
    // To avoid flickering as much as possible, create the window as close to Minecraft
    // creating its native window as possible.
    //
    // Litemods are initialised after this, init complete is called later, and forge mods
    // are called EVEN LATER.
    //
    //private void createDisplay() throws LWJGLException
    @Inject(method = "createDisplay()V", at = @At("RETURN"))
    private void onCreateDisplay(CallbackInfo info) throws LWJGLException {
        GLWindow.current();
    }
}
