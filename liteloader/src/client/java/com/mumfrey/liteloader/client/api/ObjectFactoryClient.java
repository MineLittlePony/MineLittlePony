package com.mumfrey.liteloader.client.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.integrated.IntegratedServer;

import com.mumfrey.liteloader.client.LiteLoaderEventBrokerClient;
import com.mumfrey.liteloader.client.ClientPluginChannelsClient;
import com.mumfrey.liteloader.client.GameEngineClient;
import com.mumfrey.liteloader.client.LiteLoaderPanelManager;
import com.mumfrey.liteloader.client.PacketEventsClient;
import com.mumfrey.liteloader.client.gui.startup.LoadingBar;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.core.ClientPluginChannels;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.ServerPluginChannels;
import com.mumfrey.liteloader.interfaces.PanelManager;
import com.mumfrey.liteloader.interfaces.ObjectFactory;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.permissions.PermissionsManagerClient;
import com.mumfrey.liteloader.permissions.PermissionsManagerServer;
import com.mumfrey.liteloader.util.Input;
import com.mumfrey.liteloader.util.InputManager;

/**
 * Factory for lifetime loader objects for the client side
 * 
 * @author Adam Mummery-Smith
 */
class ObjectFactoryClient implements ObjectFactory<Minecraft, IntegratedServer>
{
    private LoaderEnvironment environment;

    private LoaderProperties properties;

    private Input input;

    private LiteLoaderEventBrokerClient clientEvents;

    private PacketEventsClient clientPacketEvents;

    private GameEngineClient engine;

    private PanelManager<GuiScreen> modPanelManager;

    private ClientPluginChannelsClient clientPluginChannels;

    private ServerPluginChannels serverPluginChannels;

    ObjectFactoryClient(LoaderEnvironment environment, LoaderProperties properties)
    {
        this.environment = environment;
        this.properties = properties;
    }

    @Override
    public Input getInput()
    {
        if (this.input == null)
        {
            this.input = new InputManager(this.environment, this.properties);
        }

        return this.input;
    }

    @Override
    public LiteLoaderEventBroker<Minecraft, IntegratedServer> getEventBroker()
    {
        if (this.clientEvents == null)
        {
            this.clientEvents = new LiteLoaderEventBrokerClient(LiteLoader.getInstance(), (GameEngineClient)this.getGameEngine(), this.properties);
        }

        return this.clientEvents;
    }

    @Override
    public PacketEvents getPacketEventBroker()
    {
        if (this.clientPacketEvents == null)
        {
            this.clientPacketEvents = new PacketEventsClient();
        }

        return this.clientPacketEvents;
    }

    @Override
    public GameEngine<Minecraft, IntegratedServer> getGameEngine()
    {
        if (this.engine == null)
        {
            this.engine = new GameEngineClient();
        }

        return this.engine;
    }

    @Override
    public PanelManager<GuiScreen> getPanelManager()
    {
        if (this.modPanelManager == null)
        {
            this.modPanelManager = new LiteLoaderPanelManager(this.getGameEngine(), this.environment, this.properties);
        }

        return this.modPanelManager;
    }

    @Override
    public ClientPluginChannels getClientPluginChannels()
    {
        if (this.clientPluginChannels == null)
        {
            this.clientPluginChannels = new ClientPluginChannelsClient();
        }

        return this.clientPluginChannels;
    }

    @Override
    public ServerPluginChannels getServerPluginChannels()
    {
        if (this.serverPluginChannels == null)
        {
            this.serverPluginChannels = new ServerPluginChannels();
        }

        return this.serverPluginChannels;
    }

    @Override
    public PermissionsManagerClient getClientPermissionManager()
    {
        return PermissionsManagerClient.getInstance();
    }

    @Override
    public PermissionsManagerServer getServerPermissionManager()
    {
        return null;
    }

    @SuppressWarnings("unused")
    @Override
    public void preBeginGame()
    {
        try
        {
            Class<?> progressManagerClass = Class.forName("net.minecraftforge.fml.common.ProgressManager", false, Launch.classLoader);
            return; // Disable my loading bar if Forge's is present
        }
        catch (ClassNotFoundException ex)
        {
        }

        new LoadingBar();
    }
}
