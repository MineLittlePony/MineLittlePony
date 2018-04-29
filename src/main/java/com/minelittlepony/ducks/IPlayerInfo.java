package com.minelittlepony.ducks;

import net.minecraft.client.network.NetworkPlayerInfo;

public interface IPlayerInfo {
    /**
     * Returns true if the vanilla skin (the one returned by NetworkPlayerInfo.getSkinLocation) uses the ALEX model type.
     */
    boolean usesSlimArms();

    default NetworkPlayerInfo unwrap() {
        return (NetworkPlayerInfo)this;
    }
}
