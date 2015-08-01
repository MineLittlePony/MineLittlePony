package com.voxelmodpack.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * Utility Class for building a chat message based on StringBuilder
 * 
 * @author thatapplefreak
 * @author Gabizou
 * @author Mumfrey
 */
public class ChatMessageBuilder {
    /**
     * The message being built
     */
    private final IChatComponent queuedMessage;

    public ChatMessageBuilder() {
        this.queuedMessage = new ChatComponentText("");
    }

    /**
     * Just add some plain text into the message
     * 
     * @param text Text to add
     */
    public ChatMessageBuilder append(String text) {
        this.queuedMessage.appendSibling(new ChatComponentText(text));
        return this;
    }

    /**
     * Append some formatted text to this message
     * 
     * @param format
     * @param args
     * @return fluent interface
     */
    public ChatMessageBuilder append(String format, Object... args) {
        this.queuedMessage.appendSibling(new ChatComponentText(String.format(format, args)));
        return this;
    }

    /**
     * Add some coloured text into the message
     * 
     * @param text The text in the message
     * @param color The color in the message
     */
    public ChatMessageBuilder append(String text, EnumChatFormatting color) {
        IChatComponent addmsg = new ChatComponentText(text);
        addmsg.getChatStyle().setColor(color);
        this.queuedMessage.appendSibling(addmsg);
        return this;
    }

    /**
     * Add some formatted text into the message
     * 
     * @param text The text in the message
     * @param color The color in the message
     * @param underline Underline the text?
     */
    public ChatMessageBuilder append(String text, EnumChatFormatting color, boolean underline) {
        IChatComponent addmsg = new ChatComponentText(text);
        addmsg.getChatStyle().setColor(color);
        addmsg.getChatStyle().setUnderlined(underline);
        this.queuedMessage.appendSibling(addmsg);
        return this;
    }

    /**
     * @param comp
     * @return
     */
    public ChatMessageBuilder append(IChatComponent comp) {
        this.queuedMessage.appendSibling(this.queuedMessage);
        return this;
    }

    /**
     * Add a URL link into the message
     * 
     * @param text The Link Text
     * @param path The URL
     */
    public ChatMessageBuilder append(String text, String path, boolean onAWebsite) {
        this.append(text, EnumChatFormatting.WHITE, path, onAWebsite);
        return this;
    }

    /**
     * Add a URL link into the message with color
     * 
     * @param text The Link Text
     * @param color Color of the link
     * @param path The URL
     */
    public ChatMessageBuilder append(String text, EnumChatFormatting color, String path, boolean onAWebsite) {
        IChatComponent addmsg = new ChatComponentText(text);
        addmsg.getChatStyle().setColor(color);
        addmsg.getChatStyle().setUnderlined(true);
        addmsg.getChatStyle().setChatClickEvent(new ClickEvent(onAWebsite ? Action.OPEN_URL : Action.OPEN_FILE, path));
        this.queuedMessage.appendSibling(addmsg);
        return this;
    }

    /**
     * Put the message into chat
     */
    public void showChatMessageIngame() {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(this.queuedMessage);
    }

    /**
     * Dispatch this message as as a chat packet to the server
     * 
     * @param recipient
     */
    public void dispatch(EntityPlayerSP recipient) {
        recipient.sendChatMessage(this.getUnformattedText());
    }

    /**
     * Dispatch this message to the specified client recipients
     * 
     * @param recipient
     */
    public void dispatch(EntityPlayerMP... recipients) {
        for (EntityPlayerMP recipient : recipients) {
            if (recipient != null) {
                recipient.addChatMessage(this.queuedMessage);
            }
        }
    }

    /**
     * Get the chat message being built by this builder
     */
    public IChatComponent getMessage() {
        return this.queuedMessage;
    }

    /**
     * Get the current unformatted text string represented by this builder
     */
    public String getFormattedText() {
        return this.queuedMessage.getFormattedText();
    }

    /**
     * Get the current unformatted text string represented by this builder
     */
    public String getUnformattedText() {
        return this.queuedMessage.getUnformattedText();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getUnformattedText();
    }

    /**
     * Get the current chat component as JSON
     */
    public String toJson() {
        return IChatComponent.Serializer.componentToJson(this.queuedMessage);
    }
}