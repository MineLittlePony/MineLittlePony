package com.mumfrey.liteloader.messaging;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.InterfaceRegistrationDelegate;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.interfaces.FastIterable;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Intra-mod messaging bus, allows mods to send arbitrary notifications to each
 * other without having to create an explicit dependency or resort to reflection
 * 
 * @author Adam Mummery-Smith
 */
public final class MessageBus implements InterfaceProvider
{
    /**
     * Singleton
     */
    private static MessageBus instance;

    /**
     * Messengers subscribed to each channel
     */
    private final Map<String, FastIterable<Messenger>> messengers = new HashMap<String, FastIterable<Messenger>>();

    /**
     * Pending messages dispatched pre-startup
     */
    private final Deque<Message> messageQueue = new LinkedList<Message>();

    private boolean enableMessaging = false;

    private MessageBus()
    {
    }

    /**
     * Get the singleton instance
     */
    public static MessageBus getInstance()
    {
        if (MessageBus.instance == null)
        {
            MessageBus.instance = new MessageBus();
        }

        return MessageBus.instance;
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
        delegate.registerInterface(Messenger.class);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#initProvider()
     */
    @Override
    public void initProvider()
    {
    }

    /**
     * 
     */
    public void onStartupComplete()
    {
        this.enableMessaging = true;

        while (this.messageQueue.size() > 0)
        {
            Message msg = this.messageQueue.pop();
            this.dispatchMessage(msg);
        }
    }

    public void registerMessenger(Messenger messenger)
    {
        List<String> messageChannels = messenger.getMessageChannels();
        if (messageChannels == null)
        {
            LiteLoaderLogger.warning("Listener %s returned a null channel list for getMessageChannels(), "
                    + "this could indicate a problem with the listener", messenger.getName());
            return;
        }

        for (String channel : messageChannels)
        {
            if (channel != null && Message.isValidChannel(channel))
            {
                LiteLoaderLogger.info("Listener %s is registering MessageBus channel %s", messenger.getName(), channel);
                this.getMessengerList(channel).add(messenger);
            }
            else
            {
                LiteLoaderLogger.warning("Listener %s tried to register invalid MessageBus channel %s", messenger.getName(), channel);
            }
        }
    }

    /**
     * @param message
     */
    private void sendMessage(Message message)
    {
        if (this.enableMessaging)
        {
            this.dispatchMessage(message);
            return;
        }

        this.messageQueue.push(message);
    }

    /**
     * @param message
     */
    private void dispatchMessage(Message message)
    {
        try
        {
            FastIterable<Messenger> messengerList = this.messengers.get(message.getChannel());
            if (messengerList != null)
            {
                messengerList.all().receiveMessage(message);
            }
        }
        catch (StackOverflowError err)
        {
            // A listener tried to reply on the same channel and ended up calling itself
            throw new RuntimeException("Stack overflow encountered dispatching message on channel '"
                    + message.getChannel() + "'. Did you reply to yourself?");
        }
    }

    /**
     * Get messengers for the specified channel
     * 
     * @param channel
     */
    private FastIterable<Messenger> getMessengerList(String channel)
    {
        FastIterable<Messenger> messengerList = this.messengers.get(channel);
        if (messengerList == null)
        {
            messengerList = new HandlerList<Messenger>(Messenger.class);
            this.messengers.put(channel, messengerList);
        }

        return messengerList;
    }

    /**
     * Send an empty message on the specified channel, this is useful for
     * messages which are basically just notifications.
     * 
     * @param channel
     */
    public static void send(String channel)
    {
        Message message = new Message(channel, null, null);
        MessageBus.getInstance().sendMessage(message);
    }

    /**
     * Send a message with a value on the specified channel
     * 
     * @param channel
     * @param value
     */
    public static void send(String channel, String value)
    {
        Message message = new Message(channel, value, null);
        MessageBus.getInstance().sendMessage(message);
    }

    /**
     * Send a message with a value on the specified channel from the specified
     * sender.
     * 
     * @param channel
     * @param value
     * @param sender
     */
    public static void send(String channel, String value, Messenger sender)
    {
        Message message = new Message(channel, value, sender);
        MessageBus.getInstance().sendMessage(message);
    }

    /**
     * Send a message with a value on the specified channel from the specified
     * sender.
     * 
     * @param channel
     * @param value
     * @param sender
     * @param replyChannel
     */
    public static void send(String channel, String value, Messenger sender, String replyChannel)
    {
        Message message = new Message(channel, value, sender, replyChannel);
        MessageBus.getInstance().sendMessage(message);
    }

    /**
     * Send a message with a supplied payload on the specified channel
     * 
     * @param channel
     * @param payload
     */
    public static void send(String channel, Map<String, ?> payload)
    {
        Message message = new Message(channel, payload, null);
        MessageBus.getInstance().sendMessage(message);
    }

    /**
     * Send a message with a supplied payload on the specified channel from the
     * specified sender.
     * 
     * @param channel
     * @param payload
     * @param sender
     */
    public static void send(String channel, Map<String, ?> payload, Messenger sender)
    {
        Message message = new Message(channel, payload, sender);
        MessageBus.getInstance().sendMessage(message);
    }

    /**
     * Send a message with a supplied payload on the specified channel from the
     * specified sender.
     * 
     * @param channel
     * @param payload
     * @param sender
     * @param replyChannel
     */
    public static void send(String channel, Map<String, ?> payload, Messenger sender, String replyChannel)
    {
        Message message = new Message(channel, payload, sender, replyChannel);
        MessageBus.getInstance().sendMessage(message);
    }
}
