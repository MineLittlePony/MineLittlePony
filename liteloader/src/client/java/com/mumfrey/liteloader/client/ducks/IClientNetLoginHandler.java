package com.mumfrey.liteloader.client.ducks;

import net.minecraft.network.NetworkManager;

public interface IClientNetLoginHandler
{
    public abstract NetworkManager getNetMgr();
}
