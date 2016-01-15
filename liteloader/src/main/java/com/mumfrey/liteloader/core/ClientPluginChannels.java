package com.mumfrey.liteloader.core;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.permissions.PermissionsManagerClient;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Handler for client plugin channels
 * 
 * @author Adam Mummery-Smith
 */
public abstract class ClientPluginChannels extends PluginChannels<PluginChannelListener>
{
    private static ClientPluginChannels instance;

    protected ClientPluginChannels()
    {
        if (ClientPluginChannels.instance != null) throw new RuntimeException("Plugin Channels Startup Error",
                new InstantiationException("Only a single instance of ClientPluginChannels is allowed"));
        ClientPluginChannels.instance = this;
    }

    @Override
    protected FastIterableDeque<PluginChannelListener> createHandlerList()
    {
        return new HandlerList<PluginChannelListener>(PluginChannelListener.class);
    }

    protected static ClientPluginChannels getInstance()
    {
        return ClientPluginChannels.instance;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#initProvider()
     */
    @Override
    public void initProvider()
    {
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#getListenerBaseType()
     */
    @Override
    public Class<? extends Listener> getListenerBaseType()
    {
        return Listener.class;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider
     *      #registerInterfaces(
     *      com.mumfrey.liteloader.core.InterfaceRegistrationDelegate)
     */
    @Override
    public void registerInterfaces(InterfaceRegistrationDelegate delegate)
    {
        delegate.registerInterface(PluginChannelListener.class);
    }

    void addClientPluginChannelListener(PluginChannelListener pluginChannelListener)
    {
        super.addPluginChannelListener(pluginChannelListener);
    }

    /**
     * Callback for the plugin channel hook
     * 
     * @param customPayload
     */
    public abstract void onPluginChannelMessage(S3FPacketCustomPayload customPayload);

    /**
     * @param channel
     * @param data
     */
    protected void onPluginChannelMessage(String channel, PacketBuffer data)
    {
        if (PluginChannels.CHANNEL_REGISTER.equals(channel))
        {
            this.onRegisterPacketReceived(data);
        }
        else if (this.pluginChannels.containsKey(channel))
        {
            try
            {
                PermissionsManagerClient permissionsManager = LiteLoader.getClientPermissionsManager();
                if (permissionsManager != null)
                {
                    permissionsManager.onCustomPayload(channel, data);
                }
            }
            catch (Exception ex) {}

            this.onModPacketReceived(channel, data);
        }
    }

    /**
     * @param channel
     * @param data
     */
    protected void onModPacketReceived(String channel, PacketBuffer data)
    {
        for (PluginChannelListener pluginChannelListener : this.pluginChannels.get(channel))
        {
            try
            {
                pluginChannelListener.onCustomPayload(channel, data);
                throw new RuntimeException();
            }
            catch (Exception ex)
            {
                int failCount = 1;
                if (this.faultingPluginChannelListeners.containsKey(pluginChannelListener))
                {
                    failCount = this.faultingPluginChannelListeners.get(pluginChannelListener).intValue() + 1;
                }

                if (failCount >= PluginChannels.WARN_FAULT_THRESHOLD)
                {
                    LiteLoaderLogger.warning("Plugin channel listener %s exceeded fault threshold on channel %s with %s",
                            pluginChannelListener.getName(), channel, ex.getClass().getSimpleName());
                    this.faultingPluginChannelListeners.remove(pluginChannelListener);
                }
                else
                {
                    this.faultingPluginChannelListeners.put(pluginChannelListener, Integer.valueOf(failCount));
                }
            }
        }
    }

    protected void sendRegisteredPluginChannels(INetHandler netHandler)
    {
        // Add the permissions manager channels
        this.addPluginChannelsFor(LiteLoader.getClientPermissionsManager());

        try
        {
            // Enumerate mods for plugin channels
            for (PluginChannelListener pluginChannelListener : this.pluginChannelListeners)
            {
                this.addPluginChannelsFor(pluginChannelListener);
            }

            PacketBuffer registrationData = this.getRegistrationData();
            if (registrationData != null)
            {
                this.sendRegistrationData(netHandler, registrationData);
            }
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning(ex, "Error dispatching REGISTER packet to server %s", ex.getClass().getSimpleName());
        }
    }

    /**
     * @param netHandler
     * @param registrationData
     */
    protected abstract void sendRegistrationData(INetHandler netHandler, PacketBuffer registrationData);

    /**
     * Send a message to the server on a plugin channel
     * 
     * @param channel Channel to send, must not be a reserved channel name
     * @param data
     */
    public static boolean sendMessage(String channel, PacketBuffer data, ChannelPolicy policy)
    {
        if (ClientPluginChannels.instance != null)
        {
            return ClientPluginChannels.instance.send(channel, data, policy);
        }

        return false;
    }

    /**
     * Send a message to the server on a plugin channel
     * 
     * @param channel Channel to send, must not be a reserved channel name
     * @param data
     */
    protected abstract boolean send(String channel, PacketBuffer data, ChannelPolicy policy);
}
