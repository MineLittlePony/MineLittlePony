package com.mumfrey.liteloader.client.ducks;

public interface IFramebuffer
{
    public abstract IFramebuffer setDispatchRenderEvent(boolean dispatchRenderEvent);

    public abstract boolean isDispatchRenderEvent();
}
