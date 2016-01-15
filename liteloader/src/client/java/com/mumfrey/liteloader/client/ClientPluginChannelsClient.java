package com.mumfrey.liteloader.client;

import com.mumfrey.liteloader.client.ducks.IClientNetLoginHandler;
import com.mumfrey.liteloader.core.ClientPluginChannels;
import com.mumfrey.liteloader.core.exceptions.UnregisteredChannelException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

/**
 * Handler for client plugin channels
 * 
 * @author Adam Mummery-Smith
 */
public class ClientPluginChannelsClient extends ClientPluginChannels
{
    /**
     * @param netHandler
     * @param loginPacket
     */
    void onPostLogin(INetHandlerLoginClient netHandler, S02PacketLoginSuccess loginPacket)
    {
        this.clearPluginChannels(netHandler);
    }

    /**
     * @param netHandler
     * @param loginPacket
     */
    void onJoinGame(INetHandler netHandler, S01PacketJoinGame loginPacket)
    {
        this.sendRegisteredPluginChannels(netHandler);
    }

    /**
     * Callback for the plugin channel hook
     * 
     * @param customPayload
     */
    @Override
    public void onPluginChannelMessage(S3FPacketCustomPayload customPayload)
    {
        if (customPayload != null && customPayload.getChannelName() != null)
        {
            String channel = customPayload.getChannelName();
            PacketBuffer data = customPayload.getBufferData();

            this.onPluginChannelMessage(channel, data);
        }
    }

    /**
     * @param netHandler
     * @param registrationData
     */
    @Override
    protected void sendRegistrationData(INetHandler netHandler, PacketBuffer registrationData)
    {
        if (netHandler instanceof INetHandlerLoginClient)
        {
            NetworkManager networkManager = ((IClientNetLoginHandler)netHandler).getNetMgr();
            networkManager.sendPacket(new C17PacketCustomPayload(CHANNEL_REGISTER, registrationData));
        }
        else if (netHandler instanceof INetHandlerPlayClient)
        {
            ClientPluginChannelsClient.dispatch(new C17PacketCustomPayload(CHANNEL_REGISTER, registrationData));
        }
    }

    /**
     * Send a message to the server on a plugin channel
     * 
     * @param channel Channel to send, must not be a reserved channel name
     * @param data
     */
    @Override
    protected boolean send(String channel, PacketBuffer data, ChannelPolicy policy)
    {
        if (channel == null || channel.length() > 16 || CHANNEL_REGISTER.equals(channel) || CHANNEL_UNREGISTER.equals(channel))
        {
            throw new RuntimeException("Invalid channel name specified"); 
        }

        if (!policy.allows(this, channel))
        {
            if (policy.isSilent()) return false;
            throw new UnregisteredChannelException(channel);
        }

        C17PacketCustomPayload payload = new C17PacketCustomPayload(channel, data);
        return ClientPluginChannelsClient.dispatch(payload);
    }

    /**
     * @param payload
     */
    static boolean dispatch(C17PacketCustomPayload payload)
    {
        try
        {
            Minecraft minecraft = Minecraft.getMinecraft();

            if (minecraft.thePlayer != null && minecraft.thePlayer.sendQueue != null)
            {
                minecraft.thePlayer.sendQueue.addToSendQueue(payload);
                return true;
            }
        }
        catch (Exception ex) {}

        return false;
    }
}
