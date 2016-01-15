package com.mumfrey.liteloader.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mumfrey.liteloader.common.ducks.IChatPacket;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;

@Mixin(S02PacketChat.class)
public abstract class MixinS02PacketChat implements IChatPacket
{
    @Shadow private IChatComponent chatComponent;
    
    @Override
    public IChatComponent getChatComponent()
    {
        return this.chatComponent;
    }
    
    @Override
    public void setChatComponent(IChatComponent chatComponent)
    {
        this.chatComponent = chatComponent;
    }
}
