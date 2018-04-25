package com.minelittlepony.ducks;

import com.minelittlepony.pony.data.Pony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;

public interface IPlayerInfo {
    /**
     * Gets the pony associated with this player.
     */
    Pony getPony();

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
}
