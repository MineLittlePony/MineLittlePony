package com.voxelmodpack.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.integrated.IntegratedServer;

/**
 * Utility functions
 */
public final class Util {
    public static EntityPlayer getPlayerMP() {
        Minecraft mc = Minecraft.getMinecraft();
        IntegratedServer server = mc.getIntegratedServer();

        if (server != null) {
            return server.getConfigurationManager().getPlayerByUsername(server.getServerOwner());
        }

        return Minecraft.getMinecraft().thePlayer;
    }
}
