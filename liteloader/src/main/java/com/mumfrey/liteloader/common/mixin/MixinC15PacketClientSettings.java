package com.mumfrey.liteloader.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.common.ducks.IPacketClientSettings;

import net.minecraft.network.play.client.C15PacketClientSettings;

@Mixin(C15PacketClientSettings.class)
public abstract class MixinC15PacketClientSettings implements IPacketClientSettings
{
    @Shadow private int view;
    
    @Override
    public int getViewDistance()
    {
        return this.view;
    }
}
