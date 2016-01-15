package com.mumfrey.liteloader.core;

import io.netty.buffer.Unpooled;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import com.google.common.base.Charsets;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Manages plugin channel connections and subscriptions for LiteLoader
 *
 * @author Adam Mummery-Smith
 */
public abstract class PluginChannels<L extends CommonPluginChannelListener> implements InterfaceProvider
{
    // reserved channel consts
    protected static final String CHANNEL_REGISTER = "REGISTER";
    protected static final String CHANNEL_UNREGISTER = "UNREGISTER";

    /**
     * Number of faults for a specific listener before a warning is generated
     */
    protected static final int WARN_FAULT_THRESHOLD = 1000;

    /**
     * Mapping of plugin channel names to listeners
     */
    protected final HashMap<String, LinkedList<L>> pluginChannels = new HashMap<String, LinkedList<L>>();

    /**
     * List of mods which implement PluginChannelListener interface
     */
    protected final FastIterableDeque<L> pluginChannelListeners;

    /**
     * Plugin channels that we know the server supports
     */
    protected final Set<String> remotePluginChannels = new HashSet<String>();

    /**
     * Keep track of faulting listeners so that we can periodically log a
     * message if a listener is throwing LOTS of exceptions.
     */
    protected final Map<L, Integer> faultingPluginChannelListeners = new HashMap<L, Integer>();

    /**
     * Package private
     */
    PluginChannels()
    {
        this.pluginChannelListeners = this.createHandlerList();
    }

    /**
     * Spawn the handler list instance for this channel manager
     */
    protected abstract FastIterableDeque<L> createHandlerList();

    /**
     * Get the current set of registered client-side channels
     */
    public Set<String> getLocalChannels()
    {
        return Collections.unmodifiableSet(this.pluginChannels.keySet());
    }

    /**
     * Get the current set of registered server channels
     */
    public Set<String> getRemoteChannels()
    {
        return Collections.unmodifiableSet(this.remotePluginChannels);
    }

    /**
     * Check whether a server plugin channel is registered
     * 
     * @param channel
     * @return true if the channel is registered at the server side
     */
    public boolean isRemoteChannelRegistered(String channel)
    {
        return this.remotePluginChannels.contains(channel);
    }

    /**
     * @param pluginChannelListener
     */
    protected void addPluginChannelListener(L pluginChannelListener)
    {
        this.pluginChannelListeners.add(pluginChannelListener);
    }

    /**
     * Connecting to a new server, clear plugin channels
     * 
     * @param netHandler
     */
    protected void clearPluginChannels(INetHandler netHandler)
    {
        this.pluginChannels.clear();
        this.remotePluginChannels.clear();
        this.faultingPluginChannelListeners.clear();
    }

    /**
     * @param data
     */
    protected void onRegisterPacketReceived(PacketBuffer data)
    {
        try
        {
            byte[] bytes = new byte[data.readableBytes()];
            data.readBytes(bytes);
            String channels = new String(bytes, Charsets.UTF_8);
            for (String channel : channels.split("\u0000"))
            {
                this.remotePluginChannels.add(channel);
            }
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning(ex, "Error decoding REGISTER packet from remote host %s", ex.getClass().getSimpleName());
        }
    }

    /**
     * 
     */
    protected PacketBuffer getRegistrationData()
    {
        // If any mods have registered channels, send the REGISTER packet
        if (this.pluginChannels.keySet().size() > 0)
        {
            StringBuilder channelList = new StringBuilder();
            boolean separator = false;

            for (String channel : this.pluginChannels.keySet())
            {
                if (separator) channelList.append("\u0000");
                channelList.append(channel);
                separator = true;
            }

            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeBytes(channelList.toString().getBytes(Charsets.UTF_8));
            return buffer;
        }

        return null;
    }

    /**
     * Adds plugin channels for the specified listener to the local channels
     * collection
     * 
     * @param pluginChannelListener
     */
    protected void addPluginChannelsFor(L pluginChannelListener)
    {
        List<String> channels = pluginChannelListener.getChannels();

        if (channels != null)
        {
            for (String channel : channels)
            {
                if (channel.length() > 16 || channel.toUpperCase().equals(CHANNEL_REGISTER) || channel.toUpperCase().equals(CHANNEL_UNREGISTER))
                {
                    continue;
                }

                if (!this.pluginChannels.containsKey(channel))
                {
                    this.pluginChannels.put(channel, new LinkedList<L>());
                }

                this.pluginChannels.get(channel).add(pluginChannelListener);
            }
        }
    }

    /**
     * Policy for dispatching plugin channel packets
     *
     * @author Adam Mummery-Smith
     */
    public enum ChannelPolicy
    {
        /**
         * Dispatch the message, throw an exception if the channel is not
         * registered 
         */
        DISPATCH,

        /**
         * Dispatch the message, return false if the channel is not registered 
         */
        DISPATCH_IF_REGISTERED,

        /**
         * Dispatch the message 
         */
        DISPATCH_ALWAYS;

        /**
         * True if this policy allows outbound traffic on the specified channel
         * 
         * @param channel
         */
        public boolean allows(PluginChannels<?> channels, String channel)
        {
            if (this == ChannelPolicy.DISPATCH_ALWAYS) return true;
            return channels.isRemoteChannelRegistered(channel);
        }

        /**
         * True if this policy does not throw an exception for unregistered
         * outbound channels
         */
        public boolean isSilent()
        {
            return (this != ChannelPolicy.DISPATCH_IF_REGISTERED);
        }
    }
}
