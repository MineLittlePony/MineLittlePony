package com.mumfrey.liteloader.client.ducks;

import java.util.List;

import net.minecraft.client.resources.IResourceManagerReloadListener;

public interface IReloadable
{
    public abstract List<IResourceManagerReloadListener> getReloadListeners();
}
