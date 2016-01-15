package com.mumfrey.liteloader.client;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.world.World;

import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.common.Resources;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.resources.InternalResourcePack;

/**
 * CoreProvider which fixes SoundManager derping up at startup
 * 
 * @author Adam Mummery-Smith
 */
public class LiteLoaderCoreProviderClient implements CoreProvider
{
    /**
     * Loader Properties adapter 
     */
    private final LoaderProperties properties;

    /**
     * Read from the properties file, if true we will inhibit the sound manager
     * reload during startup to avoid getting in trouble with OpenAL.
     */
    private boolean inhibitSoundManagerReload = true;

    /**
     * If inhibit is enabled, this object is used to reflectively inhibit the
     * sound manager's reload process during startup by removing it from the
     * reloadables list.
     */
    private SoundHandlerReloadInhibitor soundHandlerReloadInhibitor;

    public LiteLoaderCoreProviderClient(LoaderProperties properties)
    {
        this.properties = properties;
    }

    @Override
    public void onInit()
    {
        this.inhibitSoundManagerReload = this.properties.getAndStoreBooleanProperty(LoaderProperties.OPTION_SOUND_MANAGER_FIX, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPostInit(GameEngine<?, ?> engine)
    {
        SimpleReloadableResourceManager resourceManager = (SimpleReloadableResourceManager)engine.getResources().getResourceManager();
        SoundHandler soundHandler = ((GameEngineClient)engine).getSoundHandler();
        this.soundHandlerReloadInhibitor = new SoundHandlerReloadInhibitor(resourceManager, soundHandler);

        if (this.inhibitSoundManagerReload)
        {
            this.soundHandlerReloadInhibitor.inhibit();
        }

        // Add self as a resource pack for texture/lang resources
        Resources<IResourceManager, IResourcePack> resources = (Resources<IResourceManager, IResourcePack>)LiteLoader.getGameEngine().getResources();
        resources.registerResourcePack(new InternalResourcePack("LiteLoader", LiteLoader.class, "liteloader"));
    }

    @Override
    public void onPostInitComplete(LiteLoaderMods mods)
    {
    }

    @Override
    public void onStartupComplete()
    {
        if (this.soundHandlerReloadInhibitor != null && this.soundHandlerReloadInhibitor.isInhibited())
        {
            this.soundHandlerReloadInhibitor.unInhibit(true);
        }
    }

    @Override
    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket)
    {
    }

    @Override
    public void onPostRender(int mouseX, int mouseY, float partialTicks)
    {
    }

    @Override
    public void onTick(boolean clock, float partialTicks, boolean inGame)
    {
    }

    @Override
    public void onWorldChanged(World world)
    {
    }

    @Override
    public void onShutDown()
    {
    }
}
