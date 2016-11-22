/*
 * This file is part of LiteLoader.
 * Copyright (C) 2012-16 Adam Mummery-Smith
 * All Rights Reserved.
 */
package com.mumfrey.liteloader.client.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mumfrey.liteloader.PlayerInteractionListener.MouseButton;
import com.mumfrey.liteloader.client.ClientProxy;
import com.mumfrey.liteloader.client.overlays.IMinecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Timer;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft
{
    @Shadow @Final private Timer timer;
    @Shadow volatile boolean running;
    @Shadow @Final private List<IResourcePack> defaultResourcePacks;
    @Shadow private String serverName;
    @Shadow private int serverPort;
    
    @Shadow abstract void resize(int width, int height);
    @Shadow private void clickMouse() {}
    @Shadow private void rightClickMouse() {}
    @Shadow private void middleClickMouse() {}
    
    @Inject(method = "init()V", at = @At("RETURN"))
    private void onStartupComplete(CallbackInfo ci)
    {
        ClientProxy.onStartupComplete();
    }
    
    @Inject(method = "updateFramebufferSize()V", at = @At("HEAD"))
    private void onResize(CallbackInfo ci)
    {
        ClientProxy.onResize((Minecraft)(Object)this);
    }
    
    @Inject(method = "runTick()V", at = @At("HEAD"))
    private void newTick(CallbackInfo ci)
    {
        ClientProxy.newTick();
    }
    
    @Inject(method = "runGameLoop()V", at = @At(
        value = "INVOKE",
        shift = Shift.AFTER,
        target = "Lnet/minecraft/client/renderer/EntityRenderer;updateCameraAndRender(FJ)V"
    ))
    private void onTick(CallbackInfo ci)
    {
        ClientProxy.onTick();
    }
    
    @Redirect(method = "runGameLoop()V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/shader/Framebuffer;framebufferRender(II)V"
    ))
    private void renderFBO(Framebuffer framebufferMc, int width, int height)
    {
        boolean fboEnabled = OpenGlHelper.isFramebufferEnabled();
        if (fboEnabled)
        {
            ClientProxy.preRenderFBO(framebufferMc);
            framebufferMc.framebufferRender(width, height);
            ClientProxy.preRenderFBO(framebufferMc);
        }
        else
        {
            framebufferMc.framebufferRender(width, height);
        }
    }
    
    @Inject(method = "runGameLoop()V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
        args = "ldc=tick"
    ))
    private void onTimerUpdate(CallbackInfo ci)
    {
        ClientProxy.onTimerUpdate();
    }
    
    @Inject(method = "runGameLoop()V", at = @At(
        value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = "ldc=gameRenderer"
    ))
    private void onRender(CallbackInfo ci)
    {
        ClientProxy.onRender();
    }
    
    @Redirect(method = "processKeyBinds()V", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/Minecraft;clickMouse()V"
    ))
    private void onClickMouse(Minecraft self)
    {
        if (ClientProxy.onClickMouse(self.player, MouseButton.LEFT))
        {
            this.clickMouse();
        }
    }
    
    @Inject(method = "sendClickBlockToController(Z)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Minecraft;objectMouseOver:Lnet/minecraft/util/math/RayTraceResult;",
            ordinal = 0
        ),
        cancellable = true
    )
    private void onMouseHeld(boolean leftClick, CallbackInfo ci)
    {
        if (!ClientProxy.onMouseHeld(((Minecraft)(Object)this).player, MouseButton.LEFT))
        {
            ci.cancel();
        }
    }
    
    @Redirect(method = "processKeyBinds()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;rightClickMouse()V",
            ordinal = 0
    ))
    private void onRightClickMouse(Minecraft self)
    {
        if (ClientProxy.onClickMouse(self.player, MouseButton.RIGHT))
        {
            this.rightClickMouse();
        }
    }
    
    @Redirect(method = "processKeyBinds()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;rightClickMouse()V",
            ordinal = 1
    ))
    private void onRightMouseHeld(Minecraft self)
    {
        if (ClientProxy.onMouseHeld(self.player, MouseButton.RIGHT))
        {
            this.rightClickMouse();
        }
    }
    
    @Redirect(method = "processKeyBinds()V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;middleClickMouse()V"
    ))
    private void onMiddleClickMouse(Minecraft self)
    {
        if (ClientProxy.onClickMouse(self.player, MouseButton.MIDDLE))
        {
            this.middleClickMouse();
        }
    }

    @Override
    public Timer getTimer()
    {
        return this.timer;
    }

    @Override
    public boolean isRunning()
    {
        return this.running;
    }

    @Override
    public List<IResourcePack> getDefaultResourcePacks()
    {
        return this.defaultResourcePacks;
    }

    @Override
    public String getServerName()
    {
        return this.serverName;
    }

    @Override
    public int getServerPort()
    {
        return this.serverPort;
    }

    @Override
    public void onResizeWindow(int width, int height)
    {
        this.resize(width, height);
    }
    
}