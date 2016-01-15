package com.mumfrey.liteloader.transformers;

import net.minecraft.network.Packet;

/**
 * Exception which a packet handler can throw in order to cancel further
 * handling of the event.
 *
 * @author Adam Mummery-Smith
 */
public class PacketHandlerException extends RuntimeException
{
    private static final long serialVersionUID = -330946238844640302L;

    private Packet packet;

    public PacketHandlerException(Packet packet)
    {
    }

    public PacketHandlerException(Packet packet, String message)
    {
        super(message);
    }

    public Packet getPacket()
    {
        return this.packet;
    }
}
