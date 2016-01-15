package com.mumfrey.liteloader.client;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.client.overlays.IEntityRenderer;
import com.mumfrey.liteloader.client.overlays.IMinecraft;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.core.event.ProfilingHandlerList;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Timer;

public class LiteLoaderEventBrokerClient extends LiteLoaderEventBroker<Minecraft, IntegratedServer> implements IResourceManagerReloadListener
{
    /**
     * Singleton 
     */
    private static LiteLoaderEventBrokerClient instance;

    /**
     * Reference to the game
     */
    protected final GameEngineClient engineClient;

    /**
     * Current screen width
     */
    private int screenWidth = 854;

    /**
     * Current screen height
     */
    private int screenHeight = 480;

    /**
     * 
     */
    private boolean wasFullScreen = false;

    /**
     * Hash code of the current world. We don't store the world reference
     * here because we don't want to mess with world GC by mistake.
     */
    private int worldHashCode = 0;

    private FastIterableDeque<Tickable>             tickListeners;
    private FastIterableDeque<GameLoopListener>     loopListeners         = new HandlerList<GameLoopListener>(GameLoopListener.class);
    private FastIterableDeque<RenderListener>       renderListeners       = new HandlerList<RenderListener>(RenderListener.class);
    private FastIterableDeque<PreRenderListener>    preRenderListeners    = new HandlerList<PreRenderListener>(PreRenderListener.class);
    private FastIterableDeque<PostRenderListener>   postRenderListeners   = new HandlerList<PostRenderListener>(PostRenderListener.class);
    private FastIterableDeque<HUDRenderListener>    hudRenderListeners    = new HandlerList<HUDRenderListener>(HUDRenderListener.class);
    private FastIterableDeque<ChatRenderListener>   chatRenderListeners   = new HandlerList<ChatRenderListener>(ChatRenderListener.class);
    private FastIterableDeque<OutboundChatListener> outboundChatListeners = new HandlerList<OutboundChatListener>(OutboundChatListener.class);
    private FastIterableDeque<ViewportListener>     viewportListeners     = new HandlerList<ViewportListener>(ViewportListener.class);
    private FastIterableDeque<FrameBufferListener>  frameBufferListeners  = new HandlerList<FrameBufferListener>(FrameBufferListener.class);
    private FastIterableDeque<InitCompleteListener> initListeners         = new HandlerList<InitCompleteListener>(InitCompleteListener.class);
    private FastIterableDeque<OutboundChatFilter>   outboundChatFilters   = new HandlerList<OutboundChatFilter>(OutboundChatFilter.class,
                                                                                                                ReturnLogicOp.AND);
    private FastIterableDeque<ScreenshotListener>   screenshotListeners   = new HandlerList<ScreenshotListener>(ScreenshotListener.class,
                                                                                                                ReturnLogicOp.AND_BREAK_ON_FALSE);
    private FastIterableDeque<EntityRenderListener> entityRenderListeners = new HandlerList<EntityRenderListener>(EntityRenderListener.class);

    @SuppressWarnings("cast")
    public LiteLoaderEventBrokerClient(LiteLoader loader, GameEngineClient engine, LoaderProperties properties)
    {
        super(loader, engine, properties);

        LiteLoaderEventBrokerClient.instance = this;

        this.engineClient = (GameEngineClient)engine;
        this.tickListeners = new ProfilingHandlerList<Tickable>(Tickable.class, this.engineClient.getProfiler());
    }

    public static LiteLoaderEventBrokerClient getInstance()
    {
        return LiteLoaderEventBrokerClient.instance;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        LoadingProgress.setMessage("Reloading Resources...");
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#registerInterfaces(
     *      com.mumfrey.liteloader.core.InterfaceRegistrationDelegate)
     */
    @Override
    public void registerInterfaces(InterfaceRegistrationDelegate delegate)
    {
        super.registerInterfaces(delegate);

        delegate.registerInterface(Tickable.class);
        delegate.registerInterface(GameLoopListener.class);
        delegate.registerInterface(RenderListener.class);
        delegate.registerInterface(PreRenderListener.class);
        delegate.registerInterface(PostRenderListener.class);
        delegate.registerInterface(HUDRenderListener.class);
        delegate.registerInterface(ChatRenderListener.class);
        delegate.registerInterface(OutboundChatListener.class);
        delegate.registerInterface(ViewportListener.class);
        delegate.registerInterface(FrameBufferListener.class);
        delegate.registerInterface(InitCompleteListener.class);
        delegate.registerInterface(OutboundChatFilter.class);
        delegate.registerInterface(ScreenshotListener.class);
        delegate.registerInterface(EntityRenderListener.class);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#initProvider()
     */
    @Override
    public void initProvider()
    {
    }

    /**
     * @param tickable
     */
    public void addTickListener(Tickable tickable)
    {
        this.tickListeners.add(tickable);
    }

    /**
     * @param loopListener
     */
    public void addLoopListener(GameLoopListener loopListener)
    {
        this.loopListeners.add(loopListener);
    }

    /**
     * @param initCompleteListener
     */
    public void addInitListener(InitCompleteListener initCompleteListener)
    {
        this.initListeners.add(initCompleteListener);
    }

    /**
     * @param renderListener
     */
    public void addRenderListener(RenderListener renderListener)
    {
        this.renderListeners.add(renderListener);
    }

    /**
     * @param preRenderListener
     */
    public void addPreRenderListener(PreRenderListener preRenderListener)
    {
        this.preRenderListeners.add(preRenderListener);
    }

    /**
     * @param postRenderListener
     */
    public void addPostRenderListener(PostRenderListener postRenderListener)
    {
        this.postRenderListeners.add(postRenderListener);
    }

    /**
     * @param chatRenderListener
     */
    public void addChatRenderListener(ChatRenderListener chatRenderListener)
    {
        this.chatRenderListeners.add(chatRenderListener);
    }

    /**
     * @param hudRenderListener
     */
    public void addHUDRenderListener(HUDRenderListener hudRenderListener)
    {
        this.hudRenderListeners.add(hudRenderListener);
    }

    /**
     * @param outboundChatListener
     */
    public void addOutboundChatListener(OutboundChatListener outboundChatListener)
    {
        this.outboundChatListeners.add(outboundChatListener);
    }

    /**
     * @param outboundChatFilter
     */
    public void addOutboundChatFiler(OutboundChatFilter outboundChatFilter)
    {
        this.outboundChatFilters.add(outboundChatFilter);
    }

    /**
     * @param viewportListener
     */
    public void addViewportListener(ViewportListener viewportListener)
    {
        this.viewportListeners.add(viewportListener);
    }

    /**
     * @param frameBufferListener
     */
    public void addFrameBufferListener(FrameBufferListener frameBufferListener)
    {
        this.frameBufferListeners.add(frameBufferListener);
    }

    /**
     * @param screenshotListener
     */
    public void addScreenshotListener(ScreenshotListener screenshotListener)
    {
        this.screenshotListeners.add(screenshotListener);
    }

    /**
     * @param entityRenderListener
     */
    public void addEntityRenderListener(EntityRenderListener entityRenderListener)
    {
        this.entityRenderListeners.add(entityRenderListener);
    }

    /**
     * Late initialisation callback
     */
    @Override
    protected void onStartupComplete()
    {
        this.engine.getResources().refreshResources(false);

        for (InitCompleteListener initMod : this.initListeners)
        {
            try
            {
                LoadingProgress.setMessage("Calling late init for mod %s...", initMod.getName());
                LiteLoaderLogger.info("Calling late init for mod %s", initMod.getName());
                initMod.onInitCompleted(this.engine.getClient(), this.loader);
            }
            catch (Throwable th)
            {
                this.mods.onLateInitFailed(initMod, th);
                LiteLoaderLogger.warning(th, "Error calling late init for mod %s", initMod.getName());
            }
        }

        this.onResize(this.engineClient.getClient());

        super.onStartupComplete();
    }

    public void onResize(Minecraft minecraft)
    {
        ScaledResolution currentResolution = this.engineClient.getScaledResolution();
        this.screenWidth = currentResolution.getScaledWidth();
        this.screenHeight = currentResolution.getScaledHeight();

        if (this.wasFullScreen != minecraft.isFullScreen())
        {
            this.viewportListeners.all().onFullScreenToggled(minecraft.isFullScreen());
        }

        this.wasFullScreen = minecraft.isFullScreen();
        this.viewportListeners.all().onViewportResized(currentResolution, minecraft.displayWidth, minecraft.displayHeight);
    }

    /**
     * Callback from the tick hook, pre render
     */
    void onRender()
    {
        this.renderListeners.all().onRender();
    }

    /**
     * Callback from the tick hook, post render entities
     * 
     * @param partialTicks
     * @param timeSlice 
     */
    void postRenderEntities(float partialTicks, long timeSlice)
    {
        this.postRenderListeners.all().onPostRenderEntities(partialTicks);
    }

    /**
     * Callback from the tick hook, post render
     * 
     * @param partialTicks 
     * @param timeSlice 
     */
    void postRender(float partialTicks, long timeSlice)
    {
        ((IEntityRenderer)this.engineClient.getClient().entityRenderer).setupCamera(partialTicks, 0);
        this.postRenderListeners.all().onPostRender(partialTicks);
    }

    /**
     * Called immediately before the current GUI is rendered
     */
    void preRenderGUI(float partialTicks)
    {
        this.renderListeners.all().onRenderGui(this.engineClient.getCurrentScreen());
    }

    /**
     * Called immediately after the world/camera transform is initialised
     * 
     * @param pass
     * @param timeSlice 
     * @param partialTicks 
     */
    void onSetupCameraTransform(int pass, float partialTicks, long timeSlice)
    {
        this.renderListeners.all().onSetupCameraTransform();
        this.preRenderListeners.all().onSetupCameraTransform(partialTicks, pass, timeSlice);
    }

    /**
     * Called immediately before the chat log is rendered
     * 
     * @param chatGui 
     * @param partialTicks 
     */
    void onRenderChat(GuiNewChat chatGui, float partialTicks)
    {
        this.chatRenderListeners.all().onPreRenderChat(this.screenWidth, this.screenHeight, chatGui);
    }

    /**
     * Called immediately after the chat log is rendered
     * 
     * @param chatGui 
     * @param partialTicks 
     */
    void postRenderChat(GuiNewChat chatGui, float partialTicks)
    {
        this.chatRenderListeners.all().onPostRenderChat(this.screenWidth, this.screenHeight, chatGui);
    }

    /**
     * Callback when about to render the HUD
     */
    void onRenderHUD(float partialTicks)
    {
        this.hudRenderListeners.all().onPreRenderHUD(this.screenWidth, this.screenHeight);
    }

    /**
     * Callback when the HUD has just been rendered
     */
    void postRenderHUD(float partialTicks)
    {
        this.hudRenderListeners.all().onPostRenderHUD(this.screenWidth, this.screenHeight);
    }

    /**
     * Callback from the tick hook, called every frame when the timer is updated
     */
    void onTimerUpdate()
    {
        Minecraft minecraft = this.engine.getClient();
        this.loopListeners.all().onRunGameLoop(minecraft);
    }

    /**
     * Callback from the tick hook, ticks all tickable mods
     */
    void onTick()
    {
        this.profiler.endStartSection("litemods");

        Timer minecraftTimer = ((IMinecraft)this.engine.getClient()).getTimer();
        float partialTicks = minecraftTimer.renderPartialTicks;
        boolean clock = minecraftTimer.elapsedTicks > 0;

        Minecraft minecraft = this.engine.getClient();

        // Flag indicates whether we are in game at the moment
        Entity renderViewEntity = minecraft.getRenderViewEntity(); // TODO OBF MCPTEST func_175606_aa - getRenderViewEntity
        boolean inGame = renderViewEntity != null && renderViewEntity.worldObj != null;

        this.profiler.startSection("loader");
        super.onTick(clock, partialTicks, inGame);

        int mouseX = Mouse.getX() * this.screenWidth / minecraft.displayWidth;
        int mouseY = this.screenHeight - Mouse.getY() * this.screenHeight / minecraft.displayHeight - 1;
        this.profiler.endStartSection("postrender");
        super.onPostRender(mouseX, mouseY, partialTicks);
        this.profiler.endSection();

        // Iterate tickable mods
        this.tickListeners.all().onTick(minecraft, partialTicks, inGame, clock);

        // Detected world change
        int worldHashCode = (minecraft.theWorld != null) ? minecraft.theWorld.hashCode() : 0;
        if (worldHashCode != this.worldHashCode)
        {
            this.worldHashCode = worldHashCode;
            super.onWorldChanged(minecraft.theWorld);
        }
    }

    /**
     * @param packet
     * @param message
     */
    void onSendChatMessage(C01PacketChatMessage packet, String message)
    {
        this.outboundChatListeners.all().onSendChatMessage(packet, message);
    }

    /**
     * @param message
     */
    void onSendChatMessage(CallbackInfo e, String message)
    {
        if (!this.outboundChatFilters.all().onSendChatMessage(message))
        {
            e.cancel();
        }
    }

    /**
     * @param framebuffer
     */
    void preRenderFBO(Framebuffer framebuffer)
    {
        this.frameBufferListeners.all().preRenderFBO(framebuffer);
    }

    /**
     * @param framebuffer
     * @param width
     * @param height
     */
    void onRenderFBO(Framebuffer framebuffer, int width, int height)
    {
        this.frameBufferListeners.all().onRenderFBO(framebuffer, width, height);
    }

    /**
     * @param framebuffer
     */
    void postRenderFBO(Framebuffer framebuffer)
    {
        this.frameBufferListeners.all().postRenderFBO(framebuffer);
    }

    /**
     * @param partialTicks
     * @param timeSlice
     */
    void onRenderWorld(float partialTicks, long timeSlice)
    {
        this.preRenderListeners.all().onRenderWorld(partialTicks);
        this.renderListeners.all().onRenderWorld();
    }

    /**
     * @param partialTicks
     * @param pass
     * @param timeSlice
     */
    void onRenderSky(float partialTicks, int pass, long timeSlice)
    {
        this.preRenderListeners.all().onRenderSky(partialTicks, pass);
    }

    /**
     * @param partialTicks
     * @param pass
     * @param renderGlobal
     */
    void onRenderClouds(float partialTicks, int pass, RenderGlobal renderGlobal)
    {
        this.preRenderListeners.all().onRenderClouds(partialTicks, pass, renderGlobal);
    }

    /**
     * @param partialTicks
     * @param pass
     * @param timeSlice
     */
    void onRenderTerrain(float partialTicks, int pass, long timeSlice)
    {
        this.preRenderListeners.all().onRenderTerrain(partialTicks, pass);
    }

    /**
     * @param e
     * @param name
     * @param width
     * @param height
     * @param fbo
     */
    void onScreenshot(CallbackInfoReturnable<IChatComponent> ci, String name, int width, int height, Framebuffer fbo)
    {
        ReturnValue<IChatComponent> ret = new ReturnValue<IChatComponent>(ci.getReturnValue());

        if (!this.screenshotListeners.all().onSaveScreenshot(name, width, height, fbo, ret))
        {
            ci.setReturnValue(ret.get());
        }
    }

    /**
     * @param source
     * @param entity
     * @param xPos
     * @param yPos
     * @param zPos
     * @param yaw
     * @param partialTicks
     * @param render 
     */
    public void onRenderEntity(RenderManager source, Entity entity, double xPos, double yPos, double zPos, float yaw, float partialTicks,
            Render render)
    {
        this.entityRenderListeners.all().onRenderEntity(render, entity, xPos, yPos, zPos, yaw, partialTicks);
    }

    /**
     * @param source
     * @param entity
     * @param xPos
     * @param yPos
     * @param zPos
     * @param yaw
     * @param partialTicks
     * @param render 
     */
    public void onPostRenderEntity(RenderManager source, Entity entity, double xPos, double yPos, double zPos, float yaw, float partialTicks,
            Render render)
    {
        this.entityRenderListeners.all().onPostRenderEntity(render, entity, xPos, yPos, zPos, yaw, partialTicks);
    }
}
