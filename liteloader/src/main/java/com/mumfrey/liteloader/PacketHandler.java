package com.mumfrey.liteloader;

import java.util.List;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

/**
 * Interface for mods which want to handle raw packets
 * 
 * @author Adam Mummery-Smith
 */
public interface PacketHandler extends LiteMod
{
    /**
     * Get list of packets to handle
     */
    public List<Class<? extends Packet>> getHandledPackets(); 

    /**
     * @param netHandler The vanilla nethandler which will handle this packet if
     *      not cancelled
     * @param packet Incoming packet
     * @return True to allow further processing of this packet, including other
     *      PacketHandlers and eventually the vanilla netHandler, to inhibit
     *      further processing return false. You may choose to return false and
     *      then invoke the vanilla handler method on the supplied INetHandler
     *      if you wish to inhibit later PacketHandlers but preserve vanilla
     *      behaviour.
     */
    public abstract boolean handlePacket(INetHandler netHandler, Packet packet);
}
