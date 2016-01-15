package com.mumfrey.liteloader.client;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.common.ducks.IChatPacket;
import com.mumfrey.liteloader.common.transformers.PacketEventInfo;
import com.mumfrey.liteloader.core.ClientPluginChannels;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker.ReturnValue;
import com.mumfrey.liteloader.core.PacketEvents;
import com.mumfrey.liteloader.core.event.EventCancellationException;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.core.runtime.Packets;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.util.ChatUtilities;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IThreadListener;

/**
 * Client-side packet event handlers
 * 
 * @author Adam Mummery-Smith
 */
public class PacketEventsClient extends PacketEvents
{
    private static RealmsServer joiningRealm;

    private FastIterableDeque<JoinGameListener>    joinGameListeners    = new HandlerList<JoinGameListener>(JoinGameListener.class);
    private FastIterableDeque<ChatListener>        chatListeners        = new HandlerList<ChatListener>(ChatListener.class);
    private FastIterableDeque<ChatFilter>          chatFilters          = new HandlerList<ChatFilter>(ChatFilter.class,
                                                                                                        ReturnLogicOp.AND_BREAK_ON_FALSE);
    private FastIterableDeque<PreJoinGameListener> preJoinGameListeners = new HandlerList<PreJoinGameListener>(PreJoinGameListener.class,
                                                                                                        ReturnLogicOp.AND_BREAK_ON_FALSE);
    private FastIterableDeque<PostLoginListener>   postLoginListeners   = new HandlerList<PostLoginListener>(PostLoginListener.class);

    @Override
    public void registerInterfaces(InterfaceRegistrationDelegate delegate)
    {
        super.registerInterfaces(delegate);

        delegate.registerInterface(JoinGameListener.class);
        delegate.registerInterface(ChatListener.class);
        delegate.registerInterface(ChatFilter.class);
        delegate.registerInterface(PreJoinGameListener.class);
        delegate.registerInterface(PostLoginListener.class);
    }

    /**
     * @param joinGameListener
     */
    public void registerJoinGameListener(JoinGameListener joinGameListener)
    {
        this.joinGameListeners.add(joinGameListener);
    }

    /**
     * @param chatFilter
     */
    public void registerChatFilter(ChatFilter chatFilter)
    {
        this.chatFilters.add(chatFilter);
    }

    /**
     * @param chatListener
     */
    public void registerChatListener(ChatListener chatListener)
    {
        if (chatListener instanceof ChatFilter)
        {
            LiteLoaderLogger.warning("Interface error initialising mod '%1s'. A mod implementing ChatFilter and ChatListener is not supported! "
                    + "Remove one of these interfaces", chatListener.getName());
        }
        else
        {
            this.chatListeners.add(chatListener);
        }
    }

    /**
     * @param joinGameListener
     */
    public void registerPreJoinGameListener(PreJoinGameListener joinGameListener)
    {
        this.preJoinGameListeners.add(joinGameListener);
    }

    /**
     * @param postLoginListener
     */
    public void registerPostLoginListener(PostLoginListener postLoginListener)
    {
        this.postLoginListeners.add(postLoginListener);
    }

    public static void onJoinRealm(long serverId, RealmsServer server)
    {
        PacketEventsClient.joiningRealm = server;
    }

    @Override
    protected IThreadListener getPacketContextListener(Packets.Context context)
    {
        if (context == Packets.Context.SERVER)
        {
            return MinecraftServer.getServer();
        }

        return Minecraft.getMinecraft();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.PacketEvents#handlePacket(
     *      com.mumfrey.liteloader.common.transformers.PacketEventInfo,
     *      net.minecraft.network.INetHandler,
     *      net.minecraft.network.play.server.S01PacketJoinGame)
     */
    @Override
    protected void handlePacket(PacketEventInfo<Packet> e, INetHandler netHandler, S01PacketJoinGame packet)
    {
        if (this.preJoinGame(e, netHandler, packet))
        {
            return;
        }

        ((INetHandlerPlayClient)netHandler).handleJoinGame(packet);
        super.handlePacket(e, netHandler, packet);

        this.postJoinGame(e, netHandler, packet);
    }

    /**
     * @param e
     * @param netHandler
     * @param packet
     * @throws EventCancellationException
     */
    private boolean preJoinGame(PacketEventInfo<Packet> e, INetHandler netHandler, S01PacketJoinGame packet) throws EventCancellationException
    {
        if (!(netHandler instanceof INetHandlerPlayClient))
        {
            return true;
        }

        e.cancel();

        return !this.preJoinGameListeners.all().onPreJoinGame(netHandler, packet);
    }

    /**
     * @param e
     * @param netHandler
     * @param packet
     */
    private void postJoinGame(PacketEventInfo<Packet> e, INetHandler netHandler, S01PacketJoinGame packet)
    {
        this.joinGameListeners.all().onJoinGame(netHandler, packet, Minecraft.getMinecraft().getCurrentServerData(), PacketEventsClient.joiningRealm);
        PacketEventsClient.joiningRealm = null;

        ClientPluginChannels clientPluginChannels = LiteLoader.getClientPluginChannels();
        if (clientPluginChannels instanceof ClientPluginChannelsClient)
        {
            ((ClientPluginChannelsClient)clientPluginChannels).onJoinGame(netHandler, packet);
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.PacketEvents#handlePacket(
     *      com.mumfrey.liteloader.common.transformers.PacketEventInfo,
     *      net.minecraft.network.INetHandler,
     *      net.minecraft.network.login.server.S02PacketLoginSuccess)
     */
    @Override
    protected void handlePacket(PacketEventInfo<Packet> e, INetHandler netHandler, S02PacketLoginSuccess packet)
    {
        if (netHandler instanceof INetHandlerLoginClient)
        {
            INetHandlerLoginClient netHandlerLoginClient = (INetHandlerLoginClient)netHandler;

            ClientPluginChannels clientPluginChannels = LiteLoader.getClientPluginChannels();
            if (clientPluginChannels instanceof ClientPluginChannelsClient)
            {
                ((ClientPluginChannelsClient)clientPluginChannels).onPostLogin(netHandlerLoginClient, packet);
            }

            this.postLoginListeners.all().onPostLogin(netHandlerLoginClient, packet);
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.PacketEvents#handlePacket(
     *      com.mumfrey.liteloader.common.transformers.PacketEventInfo,
     *      net.minecraft.network.INetHandler,
     *      net.minecraft.network.play.server.S02PacketChat)
     */
    @Override
    protected void handlePacket(PacketEventInfo<Packet> e, INetHandler netHandler, S02PacketChat packet)
    {
        if (packet.getChatComponent() == null)
        {
            return;
        }

        IChatComponent originalChat = packet.getChatComponent();
        IChatComponent chat = originalChat;
        String message = chat.getFormattedText();

        // Chat filters get a stab at the chat first, if any filter returns false the chat is discarded
        for (ChatFilter chatFilter : this.chatFilters)
        {
            ReturnValue<IChatComponent> ret = new ReturnValue<IChatComponent>();

            if (chatFilter.onChat(chat, message, ret))
            {
                if (ret.isSet())
                {
                    chat = ret.get();
                    if (chat == null)
                    {
                        chat = new ChatComponentText("");
                    }
                    message = chat.getFormattedText();
                }
            }
            else
            {
                e.cancel();
                return;
            }
        }

        if (chat != originalChat)
        {
            try
            {
                chat = ChatUtilities.convertLegacyCodes(chat);
                ((IChatPacket)packet).setChatComponent(chat);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        // Chat listeners get the chat if no filter removed it
        this.chatListeners.all().onChat(chat, message);
    }
}
