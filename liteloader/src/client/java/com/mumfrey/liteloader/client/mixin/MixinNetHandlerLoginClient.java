package com.mumfrey.liteloader.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.client.ducks.IClientNetLoginHandler;

import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;

@Mixin(NetHandlerLoginClient.class)
public abstract class MixinNetHandlerLoginClient implements IClientNetLoginHandler
{
    @Shadow private NetworkManager networkManager;
    
    @Override
    public NetworkManager getNetMgr()
    {
        return this.networkManager;
    }
}
