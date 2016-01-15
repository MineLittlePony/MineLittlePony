package com.mumfrey.liteloader.core;

import net.minecraft.server.MinecraftServer;


public interface IEventState
{
    public abstract void onTick(MinecraftServer server);
}
