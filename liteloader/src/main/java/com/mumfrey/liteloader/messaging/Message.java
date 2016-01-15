package com.mumfrey.liteloader.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

/**
 * Class used to encapsulate a MessageBus message
 * 
 * @author Adam Mummery-Smith
 */
public class Message
{
    /**
     * Regex for matching valid channels
     */
    private static final Pattern channelPattern = Pattern.compile("^[a-z0-9]([a-z0-9_\\-]*[a-z0-9])?:[a-z0-9]([a-z0-9_\\-]*[a-z0-9])?$",
                                                                    Pattern.CASE_INSENSITIVE);

    private final String channel, replyChannel;
    private final Messenger sender;
    private final Map<String, ?> payload;

    Message(String channel, Object value, Messenger sender)
    {
        this(channel, value, sender, null);
    }

    Message(String channel, Object value, Messenger sender, String replyChannel)
    {
        Message.validateChannel(channel);

        this.channel = channel;
        this.payload = ImmutableMap.<String, Object>of("value", value);
        this.sender = sender;
        this.replyChannel = replyChannel;
    }

    Message(String channel, Map<String, ?> payload, Messenger sender)
    {
        this(channel, payload, sender, null);
    }

    Message(String channel, Map<String, ?> payload, Messenger sender, String replyChannel)
    {
        Message.validateChannel(channel);

        this.channel = channel;
        this.payload = payload != null ? ImmutableMap.copyOf(payload) : ImmutableMap.<String, String>of();
        this.sender = sender;
        this.replyChannel = replyChannel;
    }

    /**
     * Get the channel (fully qualified) that this message was sent on
     */
    public String getChannel()
    {
        return this.channel;
    }

    /**
     * Get the channel category for this message
     */
    public String getCategory()
    {
        return this.channel.substring(0, this.channel.indexOf(':'));
    }

    /**
     * Get the specified reply channel (if any) for this message - may return
     * null
     */
    public String getReplyChannel()
    {
        return this.replyChannel;
    }

    /**
     * Get the message sender (if any) for this message - may return null
     */
    public Messenger getSender()
    {
        return this.sender;
    }

    /**
     * Get the message payload
     */
    public Map<String, ?> getPayload()
    {
        return this.payload;
    }

    /**
     * Check if this message is on the specified channel
     * 
     * @param channel Full name of the channel to check against (case sensitive)
     */
    public boolean isChannel(String channel)
    {
        return this.channel.equals(channel);
    }

    /**
     * Check if this message has the specified category
     * 
     * @param category
     */
    public boolean isCategory(String category)
    {
        return this.getCategory().equals(category);
    }

    /**
     * Get (and implicit cast) a value from this message's payload
     * 
     * @param key
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key)
    {
        return (T)this.payload.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue)
    {
        Object value = this.payload.get(key);
        if (value != null)
        {
            return (T)value;
        }
        return defaultValue;
    }

    /**
     * Gets the payload with the key "value", which is used with messages
     * constructed using a string-only payload.
     */
    public <T> T getValue()
    {
        return this.get("value");
    }

    public static void validateChannel(String channel) throws IllegalArgumentException
    {
        if (channel == null)
        {
            throw new IllegalArgumentException("Channel name cannot be null");
        }

        if (!Message.isValidChannel(channel))
        {
            throw new IllegalArgumentException("'" + channel + "' is not a valid channel name");
        }
    }

    public static boolean isValidChannel(String channel)
    {
        return Message.channelPattern.matcher(channel).matches();
    }

    /**
     * Build a KV map from interleaved keys and values, convenience function
     * 
     * @param args
     */
    public static Map<String, ?> buildMap(Object... args)
    {
        Map<String, Object> payload = new HashMap<String, Object>();
        for (int i = 0; i < args.length - 1; i += 2)
        {
            if (args[i] instanceof String)
            {
                payload.put((String)args[i], args[i + 1]);
            }
        }

        return payload;
    }
}
