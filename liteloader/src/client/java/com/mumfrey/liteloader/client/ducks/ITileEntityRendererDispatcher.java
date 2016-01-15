package com.mumfrey.liteloader.client.ducks;

import java.util.Map;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public interface ITileEntityRendererDispatcher
{
    public abstract Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> getSpecialRenderMap();
}
