package com.voxelmodpack.hdskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.voxelmodpack.hdskins.upload.GLWindow;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

/**
 * Mixin required to close the window after the game detects a crash.
 *
 * DO NOT REMOVE
 *
 * That means you, Killjoy.
 *
 */
@Mixin(value = Minecraft.class, priority = 9000)
public abstract class MixinMinecraft {
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
    // But senpai, what about a warden thread joined on the main? I'm sure as soon as
    // the main thread closes that would-
    //
    // Nope. It never runs.
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
    //  Update 09/06/2018:
    //     After inspecting the forge source this was found to be nothing but pure lies.
    //     The only place they call it is from FMLClientHandler#haltGame.
    //     There is still the possible case where another mod tries to call it,
    //     but that would be the very definition of 'misbehaving'.
    //
    //     Also note that the method unconditionally calls System.exit, so anyone
    //     who does will be having a hard time.
    //
    // @killjoy
    //  Don't be afraid to use a mixin when the situation calls for it.
    //  There is no other way to do this.
    //
    //  Do not remove.
    //
    //  [!!!DO NOT REMOVE!!!]
    //
    //  I'm serious, do not remove.
    //  I don't care how much of a vendeta you have aginst mixins.
    //
    //public void displayCrashReport(CrashReport crashReportIn)
    @Inject(method = "displayCrashReport(Lnet/minecraft/crash/CrashReport;)V", at = @At("HEAD"))
    private void onGameCrash(CrashReport report, CallbackInfo info) {
        GLWindow.dispose();
    }
}
