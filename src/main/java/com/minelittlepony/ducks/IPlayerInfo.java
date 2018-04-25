package com.minelittlepony.ducks;

import com.minelittlepony.pony.data.Pony;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;

public interface IPlayerInfo {
    Pony getPony();

    boolean usesSlimArms();

    public static IPlayerInfo getPlayerInfo(AbstractClientPlayer player) {
        return (IPlayerInfo)Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID());
    }
}
