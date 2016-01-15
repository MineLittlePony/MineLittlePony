package com.mumfrey.liteloader.util;

import java.util.List;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * Utility functions for chat
 *
 * @author Adam Mummery-Smith
 */
public abstract class ChatUtilities
{
    private static String formattingCodeLookup;

    static
    {
        StringBuilder formattingCodes = new StringBuilder();

        for (EnumChatFormatting chatFormat : EnumChatFormatting.values())
        {
            formattingCodes.append(chatFormat.toString().charAt(1));
        }

        ChatUtilities.formattingCodeLookup = formattingCodes.toString();
    }

    private ChatUtilities() {}

    /**
     * Get a chat style from a legacy formatting code
     * 
     * @param code Code
     * @return chat style
     */
    public static ChatStyle getChatStyleFromCode(char code)
    {
        int pos = ChatUtilities.formattingCodeLookup.indexOf(code);
        if (pos < 0) return null;
        EnumChatFormatting format = EnumChatFormatting.values()[pos];

        ChatStyle style = new ChatStyle();
        if (format.isColor())
        {
            style.setColor(format);
        }
        else if (format.isFancyStyling())
        {
            switch (format)
            {
                case BOLD: style.setBold(true); break;
                case ITALIC: style.setItalic(true); break;
                case STRIKETHROUGH: style.setStrikethrough(true); break;
                case UNDERLINE: style.setUnderlined(true); break;
                case OBFUSCATED: style.setObfuscated(true); break;
                default: return style;
            }
        }

        return style;
    }

    /**
     * Convert a component containing text formatted with legacy codes to a
     * native ChatComponent structure.
     */
    public static IChatComponent convertLegacyCodes(IChatComponent chat)
    {
        return ChatUtilities.covertCodesInPlace(chat);
    }

    private static List<IChatComponent> covertCodesInPlace(List<IChatComponent> siblings)
    {
        for (int index = 0; index < siblings.size(); index++)
        {
            siblings.set(index, ChatUtilities.covertCodesInPlace(siblings.get(index)));
        }

        return siblings;
    }

    @SuppressWarnings("unchecked")
    private static IChatComponent covertCodesInPlace(IChatComponent component)
    {
        IChatComponent newComponent = null;
        if (component instanceof ChatComponentText)
        {
            ChatComponentText textComponent = (ChatComponentText)component;
            ChatStyle style = textComponent.getChatStyle();
            String text = textComponent.getChatComponentText_TextValue();

            int pos = text.indexOf('\247');
            while (pos > -1 && text != null)
            {
                if (pos < text.length() - 1)
                {
                    IChatComponent head = new ChatComponentText(pos > 0 ? text.substring(0, pos) : "").setChatStyle(style);
                    style = ChatUtilities.getChatStyleFromCode(text.charAt(pos + 1));
                    text = text.substring(pos + 2);
                    newComponent = (newComponent == null) ? head : newComponent.appendSibling(head);
                    pos = text.indexOf('\247');
                }
                else
                {
                    text = null;
                }
            }

            if (text != null)
            {
                IChatComponent tail = new ChatComponentText(text).setChatStyle(style);
                newComponent = (newComponent == null) ? tail : newComponent.appendSibling(tail);
            }
        }

        if (newComponent == null)
        {
            ChatUtilities.covertCodesInPlace(component.getSiblings());
            return component;
        }

        for (IChatComponent oldSibling : ChatUtilities.covertCodesInPlace((List<IChatComponent>)component.getSiblings()))
        {
            newComponent.appendSibling(oldSibling);
        }

        return newComponent;
    }
}
