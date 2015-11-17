package com.voxelmodpack.common;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.PacketHandler;
import com.mumfrey.liteloader.Priority;
import com.voxelmodpack.common.interfaces.ITimeHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.ResourceLocation;

/**
 * Main mod class for VoxelCommon
 *
 * @author Adam Mummery-Smith
 */
@Priority(0)
public class LiteModVoxelCommon implements PacketHandler {
    public static final String VERSION = "2.4.0";

    public static final ResourceLocation GUIPARTS = new ResourceLocation("voxelcommon", "textures/gui/guiparts.png");

    private static List<ITimeHandler> timeHandlers = new LinkedList<ITimeHandler>();

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#getName()
     */
    @Override
    public String getName() {
        return "VoxelLib";
    }

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#getVersion()
     */
    @Override
    public String getVersion() {
        return LiteModVoxelCommon.VERSION;
    }

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#init(java.io.File)
     */
    // @SuppressWarnings("unchecked")
    @Override
    public void init(File configPath) {
        /*
         * try { Class<? extends LiteMod> voxelPacketListenerClass = (Class<?
         * extends LiteMod>)Class.forName(
         * "com.voxelmodpack.voxelpacket.client.VoxelPacketListener"); LiteMod
         * voxelPacketListener = voxelPacketListenerClass.newInstance();
         * voxelPacketListener.init(configPath);
         * LiteLoader.getInterfaceManager().registerListener(voxelPacketListener
         * ); } catch (Throwable th) { th.printStackTrace(); }
         */
    }

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#upgradeSettings(java.lang.String,
     * java.io.File, java.io.File)
     */
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

    @Override
    public List<Class<? extends Packet>> getHandledPackets() {
        return ImmutableList.<Class<? extends Packet>> of(
                S03PacketTimeUpdate.class);
    }

    @Override
    public boolean handlePacket(INetHandler netHandler, Packet packet) {
        S03PacketTimeUpdate timeUpdatePacket = (S03PacketTimeUpdate) packet;
        LiteModVoxelCommon.updateTime(netHandler, timeUpdatePacket.getTotalWorldTime(), timeUpdatePacket.getWorldTime());
        return false;
    }

    public static void registerTimeHandler(ITimeHandler timeHandler) {
        if (!LiteModVoxelCommon.timeHandlers.contains(timeHandler)) {
            LiteModVoxelCommon.timeHandlers.add(timeHandler);
        }
    }

    public static void updateTime(long totalTime, long worldTime) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            LiteModVoxelCommon.updateTime(mc.thePlayer.sendQueue, totalTime, worldTime);
        }
    }

    /**
     * @param netHandler
     * @param totalTime
     * @param worldTime
     */
    private static void updateTime(INetHandler netHandler, long totalTime, long worldTime) {
        boolean frozen = worldTime < 0L;
        worldTime = Math.abs(worldTime);

        long inTotalTime = totalTime;
        long inWorldTime = worldTime;

        for (ITimeHandler timeHandler : LiteModVoxelCommon.timeHandlers) {
            timeHandler.onTimeUpdate(totalTime, worldTime);
        }

        for (ITimeHandler timeHandler : LiteModVoxelCommon.timeHandlers) {
            if (timeHandler.isFreezingTime()) {
                frozen = true;
                totalTime = timeHandler.getFrozenTotalTime(inTotalTime);
                worldTime = timeHandler.getFrozenWorldTime(inWorldTime);
                break;
            }

            worldTime += timeHandler.getTimeOffset();
        }

        ((INetHandlerPlayClient) netHandler).handleTimeUpdate(new S03PacketTimeUpdate(totalTime, worldTime, !frozen));
    }
}
