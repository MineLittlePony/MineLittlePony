package com.mumfrey.liteloader.client;

import java.io.File;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mumfrey.liteloader.client.ducks.IFramebuffer;
import com.mumfrey.liteloader.core.Proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

/**
 * Proxy class which handles the redirected calls from the injected callbacks
 * and routes them to the relevant liteloader handler classes. We do this rather
 * than patching a bunch of bytecode into the packet classes themselves because
 * this is easier to maintain.
 * 
 * @author Adam Mummery-Smith
 */
public abstract class ClientProxy extends Proxy
{
    private static LiteLoaderEventBrokerClient broker;

    private ClientProxy() {}

    public static void onStartupComplete()
    {
        Proxy.onStartupComplete();

        ClientProxy.broker = LiteLoaderEventBrokerClient.getInstance();

        if (ClientProxy.broker == null)
        {
            throw new RuntimeException("LiteLoader failed to start up properly."
                    + " The game is in an unstable state and must shut down now. Check the developer log for startup errors");
        }

        ClientProxy.broker.onStartupComplete();
    }

    public static void onTimerUpdate()
    {
        ClientProxy.broker.onTimerUpdate();
    }

    public static void newTick()
    {
    }

    public static void onTick()
    {
        ClientProxy.broker.onTick();
    }

    public static void onRender()
    {
        ClientProxy.broker.onRender();
    }

    public static void preRenderGUI(float partialTicks)
    {
        ClientProxy.broker.preRenderGUI(partialTicks);
    }

    public static void onSetupCameraTransform(int pass, float partialTicks, long timeSlice)
    {
        ClientProxy.broker.onSetupCameraTransform(pass, partialTicks, timeSlice);
    }

    public static void postRenderEntities(int pass, float partialTicks, long timeSlice)
    {
        ClientProxy.broker.postRenderEntities(partialTicks, timeSlice);
    }

    public static void postRender(float partialTicks, long timeSlice)
    {
        ClientProxy.broker.postRender(partialTicks, timeSlice);
    }

    public static void onRenderHUD(float partialTicks)
    {
        ClientProxy.broker.onRenderHUD(partialTicks);
    }

    public static void onRenderChat(GuiNewChat chatGui, float partialTicks)
    {
        ClientProxy.broker.onRenderChat(chatGui, partialTicks);
    }

    public static void postRenderChat(GuiNewChat chatGui, float partialTicks)
    {
        ClientProxy.broker.postRenderChat(chatGui, partialTicks);
    }

    public static void postRenderHUD(float partialTicks)
    {
        ClientProxy.broker.postRenderHUD(partialTicks);
    }

    public static void onCreateIntegratedServer(IntegratedServer server, String folderName, String worldName, WorldSettings worldSettings)
    {
        ClientProxy.broker.onStartServer(server, folderName, worldName, worldSettings);
    }

    public static void onOutboundChat(CallbackInfo e, String message)
    {
        ClientProxy.broker.onSendChatMessage(e, message);
    }

    public static void onResize(Minecraft mc)
    {
        if (ClientProxy.broker == null) return;
        ClientProxy.broker.onResize(mc);
    }

    public static void preRenderFBO(Framebuffer frameBufferMc)
    {
        if (ClientProxy.broker == null) return;
        if (frameBufferMc instanceof IFramebuffer)
        {
            ((IFramebuffer)frameBufferMc).setDispatchRenderEvent(true);            
        }
        ClientProxy.broker.preRenderFBO(frameBufferMc);
    }

    public static void postRenderFBO(Framebuffer frameBufferMc)
    {
        if (ClientProxy.broker == null) return;
        ClientProxy.broker.postRenderFBO(frameBufferMc);
    }

    public static void renderFBO(Framebuffer frameBufferMc, int width, int height, boolean flag)
    {
        if (ClientProxy.broker == null) return;
        ClientProxy.broker.onRenderFBO(frameBufferMc, width, height);
    }

    public static void onRenderWorld(float partialTicks, long timeSlice)
    {
        ClientProxy.broker.onRenderWorld(partialTicks, timeSlice);
    }

    public static void onRenderSky(int pass, float partialTicks, long timeSlice)
    {
        ClientProxy.broker.onRenderSky(partialTicks, pass, timeSlice);
    }

    public static void onRenderClouds(RenderGlobal renderGlobalIn, float partialTicks, int pass)
    {
        ClientProxy.broker.onRenderClouds(partialTicks, pass, renderGlobalIn);
    }

    public static void onRenderTerrain(int pass, float partialTicks, long timeSlice)
    {
        ClientProxy.broker.onRenderTerrain(partialTicks, pass, timeSlice);
    }

    public static void onSaveScreenshot(CallbackInfoReturnable<IChatComponent> ci, File gameDir, String name, int width, int height,
            Framebuffer fbo)
    {
        ClientProxy.broker.onScreenshot(ci, name, width, height, fbo);
    }

    public static void onRenderEntity(RenderManager source, Render render, Entity entity, double x, double y, double z, float yaw, float pTicks)
    {
        ClientProxy.broker.onRenderEntity(source, entity, x, y, z, yaw, pTicks, render);
    }

    public static void onPostRenderEntity(RenderManager source, Render render, Entity entity, double x, double y, double z, float yaw, float pTicks)
    {
        ClientProxy.broker.onPostRenderEntity(source, entity, x, y, z, yaw, pTicks, render);
    }
}
