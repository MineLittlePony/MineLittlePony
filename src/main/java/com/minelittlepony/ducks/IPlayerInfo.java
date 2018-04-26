package com.minelittlepony.ducks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;

public interface IPlayerInfo {
    /**
     * Returns true if the vanilla skin (the one returned by NetworkPlayerInfo.getSkinLocation) uses the ALEX model type.
     */
    boolean usesSlimArms();

    /**
     * Gets the player info for the given player.
     */
    public static IPlayerInfo getPlayerInfo(AbstractClientPlayer player) {
        return (IPlayerInfo)Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID());
    }
    
    default NetworkPlayerInfo unwrap() {
        return (NetworkPlayerInfo)this;
    }
}
