package com.mumfrey.liteloader.common.ducks;

import net.minecraft.util.IChatComponent;

public interface IChatPacket
{
    public abstract IChatComponent getChatComponent();

    public abstract void setChatComponent(IChatComponent chatComponent);
}
